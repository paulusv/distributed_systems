package main.scala.history

import java.rmi.Naming
import java.rmi.registry.LocateRegistry

import main.scala.log.LoggingImpl

object EcgHistory {

  def main(args: Array[String]): Unit = {
    LocateRegistry.createRegistry(1099)

    val server = new LoggingImpl
    Naming.rebind("EcgHistory", server)
  }
}