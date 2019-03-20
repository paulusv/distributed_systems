package tact

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject

import akka.Done
import communication.RetrieveLog
import database.DataBase
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem, WriteOperation}
import tact.manager.ConsistencyManager
import tact.protocol.OneRound

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Replica(val replicaId: Char, val serverAddress: String, val serverList: List[String]) extends UnicastRemoteObject with RetrieveLog {

  /** Gives a name to the replica, however I am not sure if this works */
  /* TODO: Check if this works, else change to a 'parent' server containing all addresses. */
  Naming.rebind("//localhost:8080/retrieveLog", this)

  /** Each replica has a database, which will be updated by other replicas via the consistency manager */
  val dataBase = new DataBase()

  /** The writelog contains all writes that are made */
  val writeLog = new WriteLog()

  /** Will contain all conits, one for each DB entry */
  var conits: Map[Char, Conit] = Map[Char, Conit]()

  /** The consistency manager will keep track of all error variables in the replica */
  var consistencyManager = new ConsistencyManager()

  /** AntiEntropy protocol that will be used. */
  var antiEntropy = new OneRound(this)

  /**
    * Read a value for the database.
    *
    * @param key The key in the database.
    */
  def read(key: Char): Future[Option[Int]] = {
    val conit = getOrCreateConit(key)

    Future { Some(conit.getValue) }
  }

  /**
    * Write a value for the database.
    *
    * @param key   The key in the database.
    * @param value THe value that should be written to the database.
    */
  def write(key: Char, value: Int): Future[Done] = {
    val conit = getOrCreateConit(key)
    conit.update(value)
    writeLog.addItem(new WriteLogItem(System.currentTimeMillis, replicaId, new WriteOperation(key, '=', value)))

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
    * @param address The address of the remote rep  lica
    * @return
    */
  def retrieveLogFromRemote(address: String): WriteLog = {
    val rep: Replica = Naming.lookup(address).asInstanceOf[Replica]
    rep.retrieveLog()
  }
}