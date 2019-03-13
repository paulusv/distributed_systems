package communication

import java.rmi.{Remote, RemoteException}

import tact.log.WriteLog

/**
  * Trait containing the functions used by the tact replicas
  */
trait RetrieveLog extends Remote {
  @throws[RemoteException]
  def retrieveLog(): WriteLog
}
