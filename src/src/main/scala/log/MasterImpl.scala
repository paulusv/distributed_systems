package main.scala.log

import java.rmi.server.UnicastRemoteObject
import java.rmi.{Naming, Remote}
import java.time.LocalDateTime

/**
  * EcgLogImpl class.
  * Implements all the functions used with RMI for Ecg History
  */
class MasterImpl extends UnicastRemoteObject with Master {

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
    println("[" + LocalDateTime.now() + "][Master] Write ECG writelog: " + item)
    writeLog.addItem(item)
  }

  /**
    * Returns the WriteLog
    *
    * @return of type WriteLog
    */
  override def read(): WriteLog = {
    println("[" + LocalDateTime.now() + "][Master] Read ECG writelog")

    writeLog
  }

  override def debug(message: String): Unit = {
    println("[" + LocalDateTime.now() + "][Master] " + message)
  }

  override def register(name: String, obj: Remote): Unit = {
    Naming.bind(name, obj)
    println("[" + LocalDateTime.now() + "][Master] Registered " + name)
  }
}
