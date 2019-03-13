package communication

import java.rmi._
import java.rmi.server._

/**
  * Class containing the retrieve log function.
  */
class RetrieveLogRemote @throws[RemoteException]() extends UnicastRemoteObject with RetrieveLog {
  def retrieveLog(): List[String] = {

  }
}