package main.scala.tact.protocol

import java.rmi.Remote

/**
  * RoundProtocol.
  */
trait RoundProtocol extends Remote {

  /**
    * Start the round protocol.
    */
  def start()

}
