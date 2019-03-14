package tact

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject
import communication.{RetrieveLog, WriteToLog}
import tact.log.{WriteLog, WriteLogItem}

class ECGHistory extends UnicastRemoteObject with RetrieveLog with WriteToLog {

  /** The write log of the ECG history. Used by consistency managers to compare data with. **/
  var writeLog: WriteLog = new WriteLog

  /** Binds the ecg history server to a specific name, so that it can be found by other servers. **/
  try {
    val ecgHistory = new ECGHistory
    Naming.rebind("ECGHistoryServer", ecgHistory)
  } catch {
    case e: Exception =>
      System.out.println("ECGHistoryServer error: " + e.getMessage)
      e.printStackTrace()
  }

  /**
    * Adds any item to the ECG write history.
    * This does not do any checks.
    *
    * @return The write log in the ECG history.
    */
  def writeToLog(writeLogItem: WriteLogItem): Unit = {
    writeLog.addItem(writeLogItem)
  }

  /**
    * Retrieves the write log for a remote request
    *
    * @return The write log in the ECG history.
    */
  def retrieveLog(): WriteLog = {
    writeLog
  }
}
