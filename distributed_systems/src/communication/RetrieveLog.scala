package communication

import java.rmi.Remote
import java.rmi.RemoteException

/**
  * Trait containing the functions used by the tact replicas
  */
trait RetrieveLog extends Remote {
  @throws[RemoteException]
  def retrieveLog(): List[String]
}
