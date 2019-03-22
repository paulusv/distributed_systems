//package main.scala.tact.protocol
//
//import java.rmi.Naming
//
//import tact.Replica
//import tact.log.WriteLog
//import util.control.Breaks._
//
///**
//  * Two-Round protocol.
//  */
//class TwoRound(replica: Replica) extends RoundProtocol {
//
//  /**
//    * Accept the write log of another replica.
//    *
//    * @param writeLog of type WriteLog
//    */
//  override def acceptWriteLog(writeLog: WriteLog): Boolean = {
//    val log = writeLog.partition(replica.consistencyManager.logicalTimeVector)
//
//    for (item <- log.writeLogItems) {
//      breakable {
//        val conit = replica.getOrCreateConit(item.operation.key)
//
//        // Skip writes that were written to this replica.
//        if (item.replicaId.equals(replica.replicaId)) {
//          break
//        }
//
//        conit.update(item.operation.value)
//      }
//    }
//
//    true
//  }
//
//  /**
//    * Start the round protocol.
//    */
//  override def start(): Unit = {
//    for (server <- replica.serverList) {
//      val other: Replica = Naming.lookup(server).asInstanceOf[Replica]
//      val writeLog = replica.writeLog.partition(other.antiEntropy.sendTimeVector())
//
//      other.antiEntropy.acceptWriteLog(writeLog)
//    }
//
//    replica.writeToDB(writeLog)
//    replica.writeLog.flush()
//  }
//
//  /**
//    * Send the time vector to the given replica
//    */
//  override def sendTimeVector(): Int = replica.consistencyManager.logicalTimeVector
//}
