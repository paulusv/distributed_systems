package tact.protocol

import tact.Replica

/**
  * RoundProtocol.
  */
trait RoundProtocolServer {

  /**
    * Start the round protocol.
    */
  def start(replica: Replica)
}
