package main.scala.log

import java.rmi.server.UnicastRemoteObject
import java.rmi.{Naming, Remote}

import com.sun.org.slf4j.internal.{Logger, LoggerFactory}

/**
  * EcgLogImpl class.
  * Implements all the functions used with RMI for Ecg History
  */
class MasterImpl extends UnicastRemoteObject with Master {

  val logger: Logger = LoggerFactory.getLogger(classOf[Master])

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
    logger.debug("Write ECG writelog: " + item)
    writeLog.addItem(item)
  }

  /**
    * Returns the WriteLog
    *
    * @return of type WriteLog
    */
  override def read(): WriteLog = {
    logger.debug("Read ECG writelog")

    writeLog
  }

  override def debug(message: String): Unit = {
    logger.debug(message)
  }

  override def register(name: String, obj: Remote): Unit = {
    Naming.bind(name, obj)
    logger.debug("Registered " + name)
  }
}
