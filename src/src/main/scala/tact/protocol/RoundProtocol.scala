package main.scala.tact.protocol

import main.scala.log.WriteLog

/**
  * RoundProtocol.
  */
trait RoundProtocol {

  /**
    * Start the round protocol.
    */
  def start()

  /**
    * Accept the write log from a different Replica
    *
    * @param writeLog of type WriteLog
    */
  def acceptWriteLog(writeLog: WriteLog): Boolean

  /**
    * Send the time vector to the given replica
    */
  def sendTimeVector(): Int
}
