package tact.manager

import java.rmi.Naming

import tact.{ECGHistory, Replica}
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem}


class ConsistencyManager(replica: Replica) {
  var numericalError: Int = 0
  var orderError: Int = 0
  var logicalTimeVector: Int = 0



  /**
    * Checks all errors for a certain conit to see if they pass the threshold set.
    * I (Paul) think this should be done every time a value is read.
    * Then, you calculate the errors and see if they pass the set threshold.
    * If not, do some forced anti-entropy sessions.
    *
    * @param key The key(which can be used to get the conit) which you try to check errors for
    */
  def updateErrors(key: Char): Unit = {
    // TODO: Zorg dat alle errors in 1x worden geupdate/gechecked.
    var writeLog: WriteLog = new WriteLog
    try {
      val ecgHistory = Naming.lookup("rmi://localhost::8080/ECGHistoryServer").asInstanceOf[ECGHistory]
      writeLog = ecgHistory.retrieveLog()
    } catch {
      case e: Exception =>
        System.out.println("Error finding ECG History server: " + e.getMessage)
        e.printStackTrace()
    }

    numericalError = calculateNumericalRelativeError(writeLog, key)
    // TODO: Uncomment if we want to switch to absolute errors
    // numericalErrorAbsolute = calculateNumericalAbsoluteError(writeLog, key)
  }

  /**
    * Calculates the numerical error (relative)
    * num_error_relative = Fi(D_ideal) - Fi(D_observed)
    *
    * @param writeLog The ECG writelog history
    * @param key The key which has to be checked
    * @return The numerical relative error.
    */
  def calculateNumericalRelativeError(writeLog: WriteLog, key: Char): Int = {
    // Fi(Dideal) - Fi(Dobserved) == The nweight of the conit of the ECG history minus the nweight of the replica conit
    writeLog.getSummedWeightsForKey(key) - replica.getOrCreateConit(key).getValue
  }

  /**
    * Calculates the numerical error (absolute)
    * num_error_absolute = 1 - (Fi(D_ideal) / Fi(D_observed))
    *
    * @param writeLog The ECG writelog history
    * @param key The key which has to be checked
    * @return The numerical absolute error.
    */
  def calculateNumericalAbsoluteError(writeLog: WriteLog, key: Char): Double = {
    // 1- (Fi(Dideal) / Fi(Dobserved)) == The nweight of the conit of the ECG history devided by the nweight of the replica conit
    1.0 - (writeLog.getSummedWeightsForKey(key) / replica.getOrCreateConit(key).getValue)
  }

  /**
    * Determines the OWeight of the function. According to the paper:
    * Order weight: defined to be a mapping from the tuple (W, F, D) to a nonnegative real value.
    * We assume it is either 1 or 0 if the write log item corresponds to the conit key or not.
    *
    * @param W The write operation
    * @param F The conit to which the write is done
    * @return The OWeight of the write operation
    */
  def oweight(W: WriteLogItem, F: Conit): Int = {
    (W.operation.key == F.getKey).asInstanceOf[Int]
  }

  def nweight(W: WriteLogItem, F: Conit): Int = {
    //    var D_current = replica.getOrCreateConit(W.operation.key).getValue
    //    var D_ideal = D_current + W.operation.value
    //    D_ideal - D_current
    W.operation.value
  }

  // Finds the longest common prefix of history 1 and history 2
  def getPrefix(H1: WriteLog, H2: WriteLog): Int = {
    var count = 0
    for (i <- 0 to H1.writeLogItems.size){
      if (H1.writeLogItems(i) != H2.writeLogItems(i)){
        return count
      } else {
        count += 1
      }
    }
    count
  }

  // TODO: Volgens mij is dit niet meer nodig omdat het nu calculateNumericalError{Absolute, Relative} is
//  // Numerical Error (absolute) = F(D_ideal) - F(D_observed)
//  def calculateNumerical_absolute(F: Conit): Unit ={
//    var con = new Conit(0,0)
//    //ECG.getOrCreateConit(F.getKey).getValue - replica.conits.getOrElse(F.getKey, con).getValue
//  }
//
//  // Numerical Error (relative) = 1 - F(D_observed)/F(D_ideal)
//  def calculateNumerical_relative(F: Conit): Unit ={
//    var con = new Conit(0,0)
//    //1 - (replica.conits.getOrElse(F.getKey, con).getValue)/(ECG.getOrCreateConit(F.getKey).getValue)
//  }

  // Alvast de commands voor de tijd opgezocht
  def staleness(): Unit = {
    var stime = System.nanoTime()
    var rtime = System.nanoTime()
  }

}
