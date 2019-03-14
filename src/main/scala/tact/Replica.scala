package tact

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject

import akka.Done
import communication.RetrieveLog
import database.DataBase
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem, WriteOperation}
import tact.manager.ConsistencyManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Replica(replicaId: Char, timeVector: Int) extends UnicastRemoteObject with RetrieveLog {

  /** Gives a name to the replica, however I am not sure if this works **/
  try {
    val replica = new Replica(replicaId, timeVector)
    Naming.rebind("replica_" + replicaId, replica)
  } catch {
    case e: Exception =>
      System.out.println("ECGHistoryServer error: " + e.getMessage)
      e.printStackTrace()
  }

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
   var dataBase = new DataBase {}

  /** The writelog contains all writes that are made **/
   val writeLog = new WriteLog()

  /** Will contain all conits, one for each DB entry **/
   var conits = Map[Char, Conit]()

  /** The consistency manager will keep track of all error variables in the replica **/
   var consistencyManager = new ConsistencyManager(this)

  /**
    * Read a value for the database.
    *
    * @param key The key in the database.
    */
  def read(key: Char): Future[Option[Int]] = Future {
    val conit = getOrCreateConit(key)

    Option { conit.getValue }
  }

  /**
    * Write a value for the database.
    * Also writes to the ECG History
    *
    * @param key   The key in the database.
    * @param value THe value that should be written to the database.
    */
  def write(key: Char, value: Int): Future[Done] = {
    val conit = getOrCreateConit(key)
    conit.update(value)
    var writeLogItem = new WriteLogItem(timeVector, replicaId, new WriteOperation(key, '+', value))
    writeLog.addItem(writeLogItem)

    try {
      var ecgHistory = Naming.lookup("rmi://localhost::8080/ECGHistoryServer").asInstanceOf[ECGHistory]
      ecgHistory.writeToLog(writeLogItem)
    } catch {
      case e: Exception =>
        System.out.println("Error finding ECG History server: " + e.getMessage)
        e.printStackTrace()
    }

    Future { Done }
  }

  /**
    * Get or create a Conit for a key
    *
    * @param key The key in the database.
    * @return
    */
  def getOrCreateConit(key: Char): Conit = {
    val optionalConit = conits.get(key)

    var conit: Conit = null
    if (optionalConit.isEmpty) {
      conit = createConit(key)
    } else {
      conit = optionalConit.get
    }

    conit
  }

  /**
    * Create a new conit for a key
    *
    * @param key The key in the database.
    * @return
    */
  private def createConit(key: Char): Conit = {
    val conit = new Conit(key, dataBase.readValue(key))
    conits += (key -> conit)
    conit
  }

  /**
    * Retrieves the write log for a remote request
    *
    * @return
    */
  def retrieveLog(): WriteLog = {
    writeLog
  }

  /**
    * Request the write log of an remote replica
    *
    * @param address The address of the remote replica
    * @return
    */
  def retrieveLogFromRemote(address: String): WriteLog = {
    val rep: Replica = Naming.lookup(address).asInstanceOf[Replica]
    rep.retrieveLog()
  }
}