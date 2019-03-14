package communication

import java.rmi.{Remote, RemoteException}
import tact.log.WriteLogItem

/**
  * Trait containing the functions used by the ECG History
  */
trait WriteToLog extends Remote {
  @throws[RemoteException]
  def writeToLog(writeLogItem: WriteLogItem): Unit
}
