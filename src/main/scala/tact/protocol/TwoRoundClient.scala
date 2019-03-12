package tact.protocol

import tact.log.WriteLog

/**
  * Two-Round protocol.
  */
class TwoRoundClient extends RoundProtocolClient {
  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  override def acceptWriteLog(writeLog: WriteLog): Unit = {

  }
}
