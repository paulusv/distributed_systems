package main.scala.tact.protocol

import java.rmi.Naming
import java.rmi.registry.LocateRegistry

import main.scala.tact.{Tact, TactImpl}

/**
  * Two-Round protocol.
  */
class TwoRound(replica: TactImpl) extends RoundProtocol {

  /**
    * Start the round protocol.
    */
  override def start(): Unit = {
    for (server <- LocateRegistry.getRegistry().list()) {
      if (server.contains("Replica") && !server.endsWith(replica.replicaId.toString)) {
        println("Start anti-entropy session with " + server)

        val rep = Naming.lookup("rmi://localhost/" + server) match {
          case s: Tact => s
          case other => throw new RuntimeException("Error: " + other)
        }

        val writeLog = replica.writeLog.partition(rep.currentTimeFactor())
        rep.acceptWriteLog(writeLog)

        println("Finished anti-entropy session with " + server)
      }
    }

    replica.writeToDB(replica.writeLog)
    replica.writeLog.flush()
  }
}
