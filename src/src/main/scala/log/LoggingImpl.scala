package main.scala.log

import java.rmi.server.UnicastRemoteObject

class LoggingImpl extends UnicastRemoteObject with Logging {

  var items: List[String] = List[String]()

  override def write(message: String): Unit = {
    println(message)
    items = message :: items
  }

  override def read(): Unit = {
    println(items)
  }

}
