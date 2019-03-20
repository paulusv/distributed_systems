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

  /** The ecgHistory address **/
  val ecgHistoryAddress: String = "rmi://localhost:8080/ECGHistoryServer"

  /** Gives a name to the replica, however I am not sure if this works **/
  try {
    val replica = new Replica(replicaId, timeVector)
    Naming.rebind("replica_" + replicaId, replica)
  } catch {
    case e: Exception =>
      System.out.println("ECGHistoryServer error: " + e.getMessage)
      e.printStackTrace()
  }

  /** Call that implements voluntary anti-entropy **/
  checkServer()

  /** Variable that keeps track if the server is busy or not **/
  var busy: Boolean = false

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
  val dataBase: DataBase = new DataBase {}

  /** The writelog contains all writes that are made **/
   val writeLog: WriteLog = new WriteLog()

  /** Will contain all conits, one for each DB entry **/
   var conits: Map[Char, Conit] = Map[Char, Conit]()

  /** The consistency manager will keep track of all error variables in the replica **/
   var consistencyManager = new ConsistencyManager(this)

  var ecgHistory = new ECGHistory
  try {
    ecgHistory = Naming.lookup(ecgHistoryAddress).asInstanceOf[ECGHistory]
  } catch {
    case e: Exception =>
      System.out.println("Error finding ECG History server: " + e.getMessage)
      e.printStackTrace()
  }

  /**
    * Read a value for the database.
    *
    * @param key The key in the database.
    */
  def read(key: Char): Future[Option[Int]] = Future {
    busy = true

    val conit = getOrCreateConit(key)

    busy = false
    Some(conit.getValue)
  }

  /**
    * Write a value for the database.
    * Also writes to the ECG History
    *
    * @param key   The key in the database.
    * @param value THe value that should be written to the database.
    */
  def write(key: Char, value: Int): Future[Done] = {
    busy = true

    // Check ConsistencyManager errors
    if (consistencyManager.isBusy && consistencyManager.inNeedOfAntiEntropy(key)) {
      // TODO: start anti entropy session
    }

    // Creates the WriteLogItem
    val writeLogItem = createWriteLogItem(key, value)

    // Write to Conit
    writeToConit(writeLogItem)

    // Write to WriteLog
    writeLog.addItem(writeLogItem)

    // Write to ECG History
    writeToECG(writeLogItem)

    busy = false
    Future { Done }
  }

  /**
    * Creates an WriteLogItem from a key and value
    *
    * @param key The key (Char) of the Conit / write
    * @param value The value of the key should get
    * @return a WriteLogItem
    */
  def createWriteLogItem(key: Char, value: Int): WriteLogItem = {
    WriteLogItem(System.currentTimeMillis(), replicaId, WriteOperation(key, '+', value))
  }

  /**
    * Writes a WriteLogItem to the local WriteLog
    *
    * @param writeLogItem The item to be written to the WriteLog
    */
  def writeToConit(writeLogItem: WriteLogItem): Unit = {
    val conit = getOrCreateConit(writeLogItem.operation.key)
    conit.update(writeLogItem.operation.value)
  }

  /**
    * Writes a WriteLogItem to the ECG history server
    *
    * @param writeLogItem The item to be written to the ECG server
    */
  def writeToECG(writeLogItem: WriteLogItem): Unit = {
    try {
      ecgHistory.writeToLog(writeLogItem)
    } catch {
      case e: Exception =>
        System.out.println("Error finding ECG History server: " + e.getMessage)
        e.printStackTrace()
    }
  }

  /**
    * Get or create a Conit for a key
    *
    * @param key The key in the database.
    * @return
    */
  def getOrCreateConit(key: Char): Conit = {
    busy = true

    val optionalConit = conits.get(key)
    var conit: Conit = null

    if (optionalConit.isEmpty) {
      conit = createConit(key)
    } else {
      conit = optionalConit.get
    }

    busy = false
    conit
  }

  /**
    * Create a new conit for a key
    *
    * @param key The key in the database.
    * @return
    */
  private def createConit(key: Char): Conit = {
    busy = true
    val conit = new Conit(key, dataBase.readValue(key))
    conits += (key -> conit)
    busy = false
    conit
  }

  /**
    * Checks every second if server is busy
    * If not, a voluntary anti entropy session will be started
    */
  def checkServer(){
    //TODO: Implement server check

    val thread = new Thread {
      override def run(): Unit = {
        while (true) {
          //Check if server is busy
          if (!busy) {
             // TODO: do something
           }
           Thread.sleep(1000)
         }
       }
    }

    thread.run()
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