package main.scala.replica

import java.rmi.Naming

import main.scala.log.EcgLog
import main.scala.tact.TactImpl

/**
  * Class TactReplicaA
  */
object TactReplicaA {

  /**
    * Starts a Tact Replica (ID = A)
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

    val replica = new TactImpl('A', server)
    Naming.rebind("ReplicaA", replica)

    replica.read('x')
    replica.write('x', 1)
    replica.antiEntropy.start()
  }
}
