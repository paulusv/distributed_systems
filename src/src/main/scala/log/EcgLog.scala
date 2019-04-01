package main.scala.log

import java.rmi.{Remote, RemoteException}

/**
  * Trait for the Ecg History
  * For documentation, see EcgLogImpl
  */
trait EcgLog extends Remote {

  @throws(classOf[RemoteException])
  def write(item: WriteLogItem): Unit

  @throws(classOf[RemoteException])
  def read(): WriteLog

  @throws(classOf[RemoteException])
  def debug(message: String): Unit
}
