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
    val replicaId = args(0).toCharArray()(0)

    val server = Naming.lookup("rmi://localhost/EcgHistory") match {
      case s: EcgLog => s
      case _ => throw new RuntimeException("Wrong object")
    }

    val replica = new TactImpl(replicaId, server)
    Naming.rebind("Replica" + replicaId, replica)
  }
}
