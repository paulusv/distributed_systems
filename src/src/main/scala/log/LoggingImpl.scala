package main.scala.log

import java.rmi.server.UnicastRemoteObject

class LoggingImpl extends UnicastRemoteObject with Logging {

  var writeLog: WriteLog = new WriteLog()

  override def write(item: WriteLogItem): Unit = {
    println("Write ECG writelog: " + item)
    writeLog.addItem(item)

  }

  override def read(): WriteLog = {
    println("Read ECG writelog")
    writeLog
  }
}
