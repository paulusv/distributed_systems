package main.scala.tact

import java.rmi.RemoteException

import main.scala.tact.protocol.RoundProtocol

trait Tact {

  @throws(classOf[RemoteException])
  def write(key: Char, value: Int): Unit

  @throws(classOf[RemoteException])
  def read(key: Char): Int

  @throws(classOf[RemoteException])
  def getAntiEntropy: RoundProtocol

}
