package main.scala.client

import java.rmi.Naming
import java.time.LocalDateTime

import main.scala.log.Master

object History {

  /**
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val rmiServer = args(0)

    val server = Naming.lookup("//" + rmiServer + "/EcgHistory") match {
      case s: Master => s
      case other => throw new RuntimeException("Wrong objesct: " + other)
    }

    val list = server.originalValues()
    println("[" + LocalDateTime.now() + "][Master] x = " + list.head)
    println("[" + LocalDateTime.now() + "][Master] y = " + list(1))
    println("[" + LocalDateTime.now() + "][Master] z = " + list(2))
  }
}
