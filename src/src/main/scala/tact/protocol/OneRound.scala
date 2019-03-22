package main.scala.tact.protocol

import java.rmi.Naming
import java.rmi.registry.LocateRegistry

import main.scala.log.WriteLog
import main.scala.tact.{Tact, TactImpl}

import util.control.Breaks._

/**
  * One-Round protocol.
  */
class OneRound(replica: TactImpl) extends RoundProtocol {

  /**
    * Accept the write log of another replica.
    *
    * @param writeLog of type WriteLog
    */
  override def acceptWriteLog(writeLog: WriteLog): Boolean = {
    val log = writeLog.partition(replica.manager.logicalTimeVector)

    for (item <- log.writeLogItems) {
      val conit = replica.getOrCreateConit(item.operation.key)

      // Skip writes that were written to this replica.
      if (item.replicaId.equals(replica.replicaId)) {
        break
      }

      conit.update(item.operation.value)
    }

    true
  }

  /**
    * Start the round protocol.
    */
  override def start(): Unit = {
    val writeLog = replica.writeLog

    for (server <- LocateRegistry.getRegistry().list()) {
      if (server.contains("Replica") && !server.endsWith(replica.replicaId.toString)) {
        println("Start anti-entropy session with " + server)

        println( Naming.lookup("rmi://localhost/" + server).asInstanceOf)

        val rep = Naming.lookup("rmi://localhost/" + server) match {
          case s:Tact => s
          case other => throw new RuntimeException("Error: " + other)
        }

        rep.getAntiEntropy.acceptWriteLog(writeLog)

        println("Finished anti-entropy session with " + server)
      }
    }

    // TODO: write values to db
    replica.writeLog.flush()
  }

  /**
    * Send the time vector to the given replica
    */
  override def sendTimeVector(): Int = 0
}
