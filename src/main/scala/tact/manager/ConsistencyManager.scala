package tact.manager

import java.rmi.Naming

import tact.{ECGHistory, Replica}
import tact.conit.Conit
import tact.log.{WriteLog, WriteLogItem}


class ConsistencyManager(replica: Replica) {
  var isBusy: Boolean = false
  var numericalError: Int = 0
  var orderError: Int = 0
  var stalenessErrror: Int = 0
  var logicalTimeVector: Int = 0

  def inNeedOfAntiEntropy(key: Char): Boolean = {
    isBusy = true
    numericalError = 0
    orderError = 0
    logicalTimeVector = 0

    updateErrors(key, System.currentTimeMillis())

    isBusy = false
    errorsOutOfBound(key)
  }

  def errorsOutOfBound(key: Char): Boolean = {
    val conit = replica.getOrCreateConit(key)

    if (conit.numericBound < numericalError) {
      return true
    }
    if (conit.orderBound < orderError) {
      return true
    }
    if (conit.stalenessBound < stalenessErrror) {
      return true
    }

    false
  }

  /**
    * Checks all errors for a certain conit to see if they pass the threshold set.
    * I (Paul) think this should be done every time a value is read.
    * Then, you calculate the errors and see if they pass the set threshold.
    * If not, do some forced anti-entropy sessions.
    *
    * @param key The key(which can be used to get the conit) which you try to check errors for
    */
  def updateErrors(key: Char, stime: Long): Unit = {
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
    orderError = calculateOrderError(writeLog, key)
    stalenessErrror = calculateStaleness(writeLog, key, stime)
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
    * Calculates the Order Error of the current replica compared to the ECG
    *
    * @param writeLog the write log of the current replica
    * @return the order error for the current replica compared to the ECG
    */
  def calculateOrderError(writeLog: WriteLog, key: Char): Int = {
    // Find longest prefix, count oweight of all reads after that
    var ecgHistory = new ECGHistory
    try {
      ecgHistory = Naming.lookup("rmi://localhost::8080/ECGHistoryServer").asInstanceOf[ECGHistory]
    } catch {
      case e: Exception =>
        System.out.println("Error finding ECG History server: " + e.getMessage)
        e.printStackTrace()
    }
    var prefix = getPrefix(writeLog, ecgHistory.writeLog, key)
    var order_error = 0
    for (i <- prefix to writeLog.writeLogItems.size){
      order_error += oweight(writeLog.writeLogItems(i), key)
    }

    return order_error

  }


  def calculateStaleness(writeLog: WriteLog, key: Char, stime: Long): Int ={

    var ecgHistory = new ECGHistory
    try {
      ecgHistory = Naming.lookup("rmi://localhost::8080/ECGHistoryServer").asInstanceOf[ECGHistory]
    } catch {
      case e: Exception =>
        System.out.println("Error finding ECG History server: " + e.getMessage)
        e.printStackTrace()
    }

    var current = writeLog.getWriteLogItembyKey(key) // Only look at key conit
    var current_ecg = ecgHistory.writeLog.getWriteLogItembyKey(key)
    var ideal_not_observed = idealMinusObserved(current, current_ecg)

    var min = new Long
    for (i <- 0 to ideal_not_observed.writeLogItems.size) {
      if ((nweight(ideal_not_observed.writeLogItems(i), key) != 0) && (ideal_not_observed.writeLogItems(i).timeVector < stime))
        if (ideal_not_observed.writeLogItems(i).timeVector < min){ //find smallest rtime
          min = ideal_not_observed.writeLogItems(i).timeVector
        }
    }

    return (stime - min).toInt
  }

  def idealMinusObserved(log: WriteLog, ecg: WriteLog): WriteLog ={
    var writeLog = new WriteLog

    for (writeLogItem <- ecg.writeLogItems){
      if(!(log.contains(writeLogItem))){
        writeLog.addItem(writeLogItem)
      }
    }
    writeLog
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
  def oweight(W: WriteLogItem, F: Char): Int = {
    (W.operation.key == F).asInstanceOf[Int]
  }

  def nweight(W: WriteLogItem, F: Char): Int = {
    //    var D_current = replica.getOrCreateConit(W.operation.key).getValue
    //    var D_ideal = D_current + W.operation.value
    //    D_ideal - D_current
    W.operation.value
  }

  // Finds the longest common prefix of history 1 and history 2
  def getPrefix(H1: WriteLog, H2: WriteLog, key: Char ): Int = {
    var count = 0
    var hist1 = H1.getWriteLogItembyKey(key)
    var hist2 = H2.getWriteLogItembyKey(key)

    for (i <- 0 to hist1.writeLogItems.size) {
      if (hist1.writeLogItems(i) != hist2.writeLogItems(i)) {
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

}
