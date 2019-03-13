package tact.manager

import tact.Replica
import tact.conit.Conit
import tact.log.WriteLogItem


class ConsistencyManager(replica: Replica) {
  var numericalError: Int = 0
  var orderError: Int = 0
  var logicalTimeVector: Int = 0


  def nweight(W: WriteLogItem, F: Conit): Int ={
    var D_current = replica.getOrCreateConit(W.operation.key).getValue // Current value of the conit
    var D_ideal = D_current + W.operation.value
    D_ideal - D_current
  }

  def oweight(W: WriteLogItem, F: Conit): Unit ={
  }

}
