package communication

import java.rmi._
import java.rmi.server._

import tact.log.WriteLog

/**
  * Class containing the retrieve log function.
  */
class RetrieveLogRemote @throws[RemoteException]() extends UnicastRemoteObject with RetrieveLog {
  def retrieveLog(): WriteLog = {
    new WriteLog
  }
}
