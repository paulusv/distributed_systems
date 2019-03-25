package main.scala.log

import java.rmi.server.UnicastRemoteObject

/**
  * EcgLogImpl class.
  * Implements all the functions used with RMI for Ecg History
  */
class EcgLogImpl extends UnicastRemoteObject with EcgLog {

  /**
    * The writeLog contains all writes that are made
    */
  var writeLog: WriteLog = new WriteLog()

  /**
    * Writes an WriteLogItem to the writeLog
    *
    * @param item of type WriteLogItem
    */
  override def write(item: WriteLogItem): Unit = {
    println("Write ECG writelog: " + item)
    writeLog.addItem(item)
  }

  /**
    * Returns the WriteLog
    *
    * @return of type WriteLog
    */
  override def read(): WriteLog = {
    println("Read ECG writelog")
    writeLog
  }
}
