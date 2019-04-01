package main.scala.replica

import java.rmi.Naming

import main.scala.log.EcgLog
import main.scala.tact.TactImpl

/**
  * Class TactReplicaA
  */
object TactReplica {

  /**
    * Starts a Tact Replica (ID = A)
    * Looks up the ECG and gives it to the created tact
    * Does some basic write and read operations
    *
    * @param args of type Array[String]
    */
  def main(args: Array[String]): Unit = {
    val rmiServer = args(0)
    val replicaId = args(1).toCharArray()(0)

    val server = Naming.lookup("rmi://" + rmiServer + "/EcgHistory") match {
      case s: EcgLog => s
      case other => throw new RuntimeException("Wrong object: " + other)
    }

    val replica = new TactImpl(replicaId, server)
    Naming.rebind("Replica" + replicaId, replica)
  }
}
