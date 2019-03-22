package main.scala.tact

import java.rmi.Naming

import main.scala.log.Logging

object TactReplica {

  def main(args: Array[String]): Unit = {
    val server = Naming.lookup("rmi://localhost/EcgHistory") match {
      case s: Logging => s
      case _ => throw new RuntimeException("Wrong object")
    }

    server.write("test")
  }
}
