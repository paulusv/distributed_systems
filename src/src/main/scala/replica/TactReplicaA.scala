package main.scala.replica

import java.rmi.Naming

import main.scala.log.Logging
import main.scala.tact.TactImpl

object TactReplicaA {

  def main(args: Array[String]): Unit = {
    val server = Naming.lookup("rmi://localhost/EcgHistory") match {
      case s: Logging => s
      case _ => throw new RuntimeException("Wrong object")
    }

    val replica = new TactImpl('A', server)
    Naming.rebind("ReplicaA", replica)

    replica.read('x')
    replica.write('x', 1)
    replica.antiEntropy.start()
  }
}
