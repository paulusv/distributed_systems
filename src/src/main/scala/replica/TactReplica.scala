package main.scala.replica

import java.rmi.Naming

import com.sun.org.slf4j.internal.{Logger, LoggerFactory}
import main.scala.log.Master
import main.scala.tact.{Tact, TactImpl}

/**
  * Class TactReplicaA
  */
object TactReplica {

  val logger: Logger = LoggerFactory.getLogger(classOf[Tact])

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

    logger.debug("Starting Replica" + replicaId);

    logger.debug("=> Looking for ECG history")
    val server = Naming.lookup("//" + rmiServer + "/EcgHistory") match {
      case s: Master => s
      case other => throw new RuntimeException("Wrong objesct: " + other)
    }
    server.debug("Registered Replica" + replicaId)

    logger.debug("=> Binding TACT Replica to RMI")
    val replica = new TactImpl(replicaId, server)
    server.register("//" + rmiServer + "/Replica" + replicaId, replica)

    logger.debug("Replica started on " + "rmi://" + rmiServer + "/Replica" + replicaId)
  }
}
