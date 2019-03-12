package tact.protocol

import tact.log.WriteLog

/**
  * One-Round protocol.
  */
class OneRoundClient extends RoundProtocolClient {
  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  override def acceptWriteLog(writeLog: WriteLog): Unit = {

  }
}
