package main.scala.log

import java.rmi.{Remote, RemoteException}

trait Logging extends Remote {

  @throws(classOf[RemoteException])
  def write(message: String): Unit

  @throws(classOf[RemoteException])
  def read(): Unit

}
