package main.scala.history

import java.rmi.Naming
import java.rmi.registry.LocateRegistry

import main.scala.log.EcgLogImpl

/**
  * ECG History server class
  */
object EcgHistory {

  /**
    * Creates an registry for RMI and binds itself as EcgHistory
    *
    * @param args of type Array[String]
    */
  def main(args: Array[String]): Unit = {
    LocateRegistry.createRegistry(1099)

    val server = new EcgLogImpl
    Naming.rebind("EcgHistory", server)
  }
}