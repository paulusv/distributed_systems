package main.scala.tact

import java.rmi.{Remote, RemoteException}

import main.scala.tact.protocol.RoundProtocol

/**
  * Trait for the Tact Replicas
  * For documentation, see TactImplImpl
  */
trait Tact extends Remote {

  @throws(classOf[RemoteException])
  def write(key: Char, value: Int): Unit

  @throws(classOf[RemoteException])
  def read(key: Char): Int

  @throws(classOf[RemoteException])
  def getAntiEntropy: RoundProtocol

}
