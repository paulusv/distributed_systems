package main.scala.replica

import java.rmi.Naming

import main.scala.log.EcgLog
import main.scala.tact.TactImpl

/**
  * TactReplicaB class
  */
object TactReplicaB {

  /**
    * Starts a Tact Replica (ID = B)
    * Looks up the ECG and gives it to the created tact
    * Does some basic write and read operations
    *
    * @param args of type Array[String]
    */
  def main(args: Array[String]): Unit = {
    val server = Naming.lookup("rmi://localhost/EcgHistory") match {
      case s: EcgLog => s
      case _ => throw new RuntimeException("Wrong object")
    }

    val replica = new TactImpl('B', server)
    Naming.rebind("ReplicaB", replica)

    replica.write('y', 1)
    replica.read('y')
    replica.antiEntropy.start()
  }
}
