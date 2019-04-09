package main.scala.client

import java.rmi.Naming

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

    server.originalValues()
  }
}
