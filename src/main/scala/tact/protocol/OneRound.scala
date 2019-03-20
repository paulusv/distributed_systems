package tact.protocol

import java.rmi.Naming

import tact.Replica
import tact.log.WriteLog

/**
  * One-Round protocol.
  */
class OneRound(replica: Replica) extends RoundProtocol {

  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  override def acceptWriteLog(writeLog: WriteLog): Boolean = {
    val log = writeLog.partition(replica.consistencyManager.logicalTimeVector)

    for (item <- log.writeLogItems) {
      val conit = replica.getOrCreateConit(item.operation.key)
      conit.update(item.operation.value)
    }

    true
  }

  /**
    * Start the round protocol.
    */
  override def start(): Unit = {
    val writeLog = replica.writeLog.partition(replica.consistencyManager.logicalTimeVector)

    for (server <- replica.servers) {
      val rep: Replica = Naming.lookup(server).asInstanceOf[Replica]
      rep.antiEntropy.acceptWriteLog(writeLog)
    }
  }

  /**
    * Send the time vector to the given replica
    */
  override def sendTimeVector(): Int = 0
}