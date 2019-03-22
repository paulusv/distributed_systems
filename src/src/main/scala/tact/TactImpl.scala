package main.scala.tact

import java.rmi.server.UnicastRemoteObject

import main.scala.database.Database
import main.scala.log.{EcgLog, WriteLog, WriteLogItem, WriteOperation}
import main.scala.tact.conit.Conit
import main.scala.tact.manager.ConsistencyManager
import main.scala.tact.protocol.{OneRound, RoundProtocol}

/**
  * TactImpl class.
  * Implements all the functions used with RMI for Tact Replicas
  *
  * @param replicaId  The identifier of the replica
  * @param ecgHistory The ECG history the replica uses to update errors
  */
class TactImpl(val replicaId: Char, val ecgHistory: EcgLog) extends UnicastRemoteObject with Tact {

  /**
    * The writeLog contains all writes that are made
    */
  var writeLog: WriteLog = new WriteLog()

  /**
    * Will contain all conits, one for each DB entry
    */
  var conits: Map[Char, Conit] = Map[Char, Conit]()

  /**
    * Each replica has a database, which will be updated by other replicas via the consistency manager
    */
  var database = new Database()

  /**
    * The consistency manager will keep track of all error variables in the replica
    */
  var manager = new ConsistencyManager(this)

  /**
    * AntiEntropy protocol that will be used.
    */
  var antiEntropy = new OneRound(this)

  /**
    * Write item to replica. Writes to the conit, the writeLog and the ecgHistory
    *
    * @param key   The key which should be written
    * @param value The value which should be written
    */
  override def write(key: Char, value: Int): Unit = {
    if (manager.inNeedOfAntiEntropy(key)) {
      // TODO: anti-entropy
    }

    val conit = getOrCreateConit(key)

    conit.update(value)
    writeLog.addItem(WriteLogItem(System.currentTimeMillis(), replicaId, WriteOperation(key, '+', value)))
    ecgHistory.write(WriteLogItem(System.currentTimeMillis(), replicaId, WriteOperation(key, '+', value)))
  }

  /**
    * Reads the value of a specific conit
    *
    * @param key The key of the conit to be read
    * @return The value in the conit
    */
  override def read(key: Char): Int = {
    val conit = getOrCreateConit(key)
    conit.value
  }


  /**
    * Get or create a conit for a key
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
    val conit = new Conit(key, database.readValue(key))

    conits += (key -> conit)
    conit
  }

  /**
    * Returns the RoundProtocol in the replica
    *
    * @return of type RoundProtocol
    */
  override def getAntiEntropy: RoundProtocol = antiEntropy
}
