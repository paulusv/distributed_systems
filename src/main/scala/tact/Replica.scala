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
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class Replica(replicaId: Char, timeVector: Int) extends UnicastRemoteObject with RetrieveLog {

  /** Gives a name to the replica, however I am not sure if this works **/
  /* TODO: Check if this works, else change to a 'parent' server containing all adresses. */
  Naming.rebind("//localhost:8080/retrieveLog", this)

  /** Call that implements voluntary anti-entropy **/
  checkServer()

  private var busy: Boolean = false

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
  private val dataBase = new DataBase {}

  /** The writelog contains all writes that are made **/
  private val writeLog = new WriteLog()

  /** Will contain all conits, one for each DB entry **/
  private var conits = Map[Char, Conit]()

  /** The consistency manager will keep track of all error variables in the replica **/
  private var consistencyManager = new ConsistencyManager()

  /**
    * Read a value for the database.
    *
    * @param key The key in the database.
    */
  def read(key: Char): Future[Option[Int]] = Future {
    busy = true
    val conit = getOrCreateConit(key)

    busy = false
    Future { conit.getValue }
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
    writeLog.addItem(new WriteLogItem(timeVector, replicaId, new WriteOperation(key, '+', value)))

    Future { Done }
  }

  /**
    * Get or create a Conit for a key
    *
    * @param key The key in the database.
    * @return
    */
  private def getOrCreateConit(key: Char): Conit = {
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
    * Checks every second if server is busy
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