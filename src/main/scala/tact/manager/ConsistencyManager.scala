package tact.manager

import tact.Replica
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem}


class ConsistencyManager(replica: Replica) {
  var numericalError: Int = 0
  var orderError: Int = 0
  var logicalTimeVector: Int = 0


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

}
