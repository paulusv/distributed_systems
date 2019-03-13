package tact.manager

import tact.Replica
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem}


class ConsistencyManager(replica: Replica) {
  var numericalError: Int = 0
  var orderError: Int = 0
  var logicalTimeVector: Int = 0
  var ECG: Replica = null


  def nweight(W: WriteLogItem, F: Conit): Int ={
//    var D_current = replica.getOrCreateConit(W.operation.key).getValue
//    var D_ideal = D_current + W.operation.value
//    D_ideal - D_current
    W.operation.value
  }

  def oweight(W: WriteLogItem, F: Conit): Unit ={

    if (W.operation.key == F.getKey){
      1
    }
    else {
      0
    }
  }

  // Finds the longest common prefix of history 1 and history 2
  def getPrefix(H1: WriteLog, H2: WriteLog) ={
    var count = 0
    for (i <- 0 to H1.writeLogItems.size){
      if (H1.writeLogItems(i) != H2.writeLogItems(i)){
        count
      }
      else{
        count += 1
      }
    }
    count
  }

  // Numerical Error (absolute) = F(D_ideal) - F(D_observed)
  def calculateNumerical_absolute(F: Conit) ={
    var con = new Conit(0,0)
    ECG.getOrCreateConit(F.getKey).getValue - replica.conits.getOrElse(F.getKey, con).getValue
  }

  // Numerical Error (relative) = 1 - F(D_observed)/F(D_ideal)
  def calculateNumerical_relative(F: Conit) ={
    var con = new Conit(0,0)
    1 - (replica.conits.getOrElse(F.getKey, con).getValue)/(ECG.getOrCreateConit(F.getKey).getValue)
  }

  // Alvast de commands voor de tijd opgezocht
  def staleness() ={
    var stime = System.nanoTime()
    var rtime = System.nanoTime()
  }

}
