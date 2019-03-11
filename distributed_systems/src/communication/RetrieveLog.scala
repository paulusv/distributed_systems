package communication

import java.rmi.Remote
import java.rmi.RemoteException


trait RetrieveLog extends Remote {
  @throws[RemoteException]
  def retrieveLog(): List[String]
}
