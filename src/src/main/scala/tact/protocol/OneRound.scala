package main.scala.tact.protocol

import java.rmi.Naming
import java.rmi.registry.LocateRegistry

import main.scala.tact.{Tact, TactImpl}

/**
  * One-Round protocol.
  */
class OneRound(replica: TactImpl) extends Serializable with RoundProtocol {

  /**
    * Start the round protocol.
    */
  override def start(key: Char): Unit = {
    val writeLog = replica.writeLog.getWriteLogForKey(key)

    for (server <- LocateRegistry.getRegistry().list()) {
      if (server.contains("Replica") && !server.endsWith(replica.replicaId.toString)) {
        println("Start anti-entropy session with " + server)

        val rep = Naming.lookup("rmi://localhost/" + server) match {
          case s: Tact => s
          case other => throw new RuntimeException("Error: " + other)
        }

        rep.acceptWriteLog(key, writeLog)

        println("Finished anti-entropy session with " + server)
      }
    }

    replica.writeToDB(writeLog)
    replica.writeLog.flush()
  }
}
