package main.scala.tact

import java.rmi.server.UnicastRemoteObject

import main.scala.database.Database
import main.scala.log.{Logging, WriteLog, WriteLogItem, WriteOperation}
import main.scala.tact.conit.Conit
import main.scala.tact.manager.ConsistencyManager
import main.scala.tact.protocol.{OneRound, RoundProtocol}

class TactImpl(val replicaId: Char, val ecgHistory: Logging) extends UnicastRemoteObject with Tact {

  var writeLog: WriteLog = new WriteLog()

  var conits: Map[Char, Conit] = Map[Char, Conit]()

  var database = new Database()

  var manager = new ConsistencyManager(this)

  // Anti entopy
  var antiEntropy = new OneRound(this)

  /**
    * Write item to replica.
    *
    * @param key   of type String
    * @param value of type Int
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

  override def read(key: Char): Int = {
    println(key)
    val conit = getOrCreateConit(key)

    conit.value
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
    val conit = new Conit(key, database.readValue(key))

    conits += (key -> conit)
    conit
  }

  override def getAntiEntropy: RoundProtocol = antiEntropy
}
