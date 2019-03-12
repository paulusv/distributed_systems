package tact.protocol

import tact.log.WriteLog

/**
  * RoundProtocolClient accept the writeLog of and other
  */
trait RoundProtocolClient {

  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  def acceptWriteLog(writeLog: WriteLog): Unit

}
