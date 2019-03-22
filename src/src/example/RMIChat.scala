package example

import java.rmi.server.UnicastRemoteObject
import java.rmi.{Naming, Remote, RemoteException}

trait RemoteChat extends Remote {

  @throws(classOf[RemoteException])
  def receiveMessage(message: String): Unit

  @throws(classOf[RemoteException])
  def newUser(name: String): Unit

}

class RemoteChatImpl extends UnicastRemoteObject with RemoteChat {

  override def receiveMessage(message: String): Unit = {
    println(message)
  }

  override def newUser(name: String): Unit = {
    println(name + " has arrived.")
  }

}

object RMIChat {

  def main(args: Array[String]): Unit = {
    val server = Naming.lookup("rmi://localhost/ECGHistory") match {
      case s: EcgHistory => s
      case _ => throw new RuntimeException("Bound object is wrong")
    }

    server.sendMessage("Test")
  }
}
