package tact.protocol

import tact.Replica
import tact.log.WriteLog

/**
  * Two-Round protocol.
  */
class TwoRound(replica: Replica) extends RoundProtocol {
  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  override def acceptWriteLog(writeLog: WriteLog): Unit = {

  }

  /**
    * Start the round protocol.
    */
  override def start(replica: Replica): Unit = {

  }
}
