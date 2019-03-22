package example

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.rmi.{Naming, Remote, RemoteException}

trait Logging extends Remote {

  @throws(classOf[RemoteException])
  def write(item: String): Unit

  @throws(classOf[RemoteException])
  def read(): List[String]

}

class LoggingImpl extends UnicastRemoteObject with Logging {

  var items : List[String]

  def write(item: String): Unit = {
    println(item)
    items = item :: items
  }

  def read(): List[String] = {
    items
  }

}

object EcgHistory {

  def main(args: Array[String]): Unit = {
    LocateRegistry.createRegistry(1099)
    val server = new LoggingImpl
    Naming.rebind("RMIChatServer", server)
  }

}
