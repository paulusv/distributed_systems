package communication

import java.rmi._
import java.rmi.server._

class RetrieveLogRemote @throws[RemoteException]() extends UnicastRemoteObject with RetrieveLog {
  def retrieveLog(): List[String] = {
    Nil
  }
}