package tact.protocol

import tact.Replica

/**
  * One-Round protocol.
  */
class OneRoundServer extends RoundProtocolServer {

  /**
    * Start the round protocol.
    */
  override def start(replica: Replica): Unit = {
    propagateWrite(replica)
    replica.writeLog.flush()
  }

  /**
    * Propagating the write the the different replicas.
    */
  def propagateWrite(replica: Replica): Unit = {
    // TODO: propagate all the writes
  }
}
