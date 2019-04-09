package main.scala.history

import java.rmi.registry.LocateRegistry

import main.scala.log.MasterImpl

/**
  * ECG History server class
  */
object MasterReplica {

  /**
    * Creates an registry for RMI and binds itself as EcgHistory
    *
    * @param args of type Array[String]
    */
  def main(args: Array[String]): Unit = {
    println("Starting...")
    val registry = LocateRegistry.createRegistry(1099)
    println("=> Created registry")

    val server = new MasterImpl
    registry.rebind("EcgHistory", server)
    println("=> Bind ECG history")

    println("RMI Registry started!")
    println("Use Crtl+C to stop the server")
  }
}