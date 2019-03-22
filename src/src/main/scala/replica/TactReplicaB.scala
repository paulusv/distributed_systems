package main.scala.replica

import java.rmi.Naming

import main.scala.log.Logging
import main.scala.tact.TactImpl

object TactReplicaB {

  def main(args: Array[String]): Unit = {
    val server = Naming.lookup("rmi://localhost/EcgHistory") match {
      case s: Logging => s
      case _ => throw new RuntimeException("Wrong object")
    }

    val replica = new TactImpl('B', server)
    Naming.rebind("ReplicaB", replica)

    replica.write('y', 1)
    replica.read('y')
    replica.antiEntropy.start()
  }
}
