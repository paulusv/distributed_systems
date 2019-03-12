package tact

import akka.Done
import database.DataBase
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem, WriteOperation}
import tact.manager.ConsistencyManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Replica(replicaId: Char, timeVector: Int) {

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
  private var dataBase = new DataBase {}

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
    val conit = getOrCreateConit(key)

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
    writeLog.addItem(new WriteLogItem(timeVector, replicaId, new WriteOperation(key, '=', value)))

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
}