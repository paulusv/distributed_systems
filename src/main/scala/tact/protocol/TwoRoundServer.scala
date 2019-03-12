package tact.protocol

import tact.Replica

/**
  * Two-Round protocol.
  */
class TwoRoundServer extends RoundProtocolServer {

  /**
    * Start the round protocol.
    */
  override def start(replica: Replica): Unit = {
    val logicalTimeVector = fetchLogicalTimeVector()
    propagateWrite(logicalTimeVector)
  }

  /**
    * Fetch the logical time vector
    */
  def fetchLogicalTimeVector(): Int = {
    0
  }

  /**
    * Propagating the write the the different replicas.
    */
  def propagateWrite(timeVector: Int): Unit = {

  }
}
