package log

/**
  * WriteLog
  * When written to the log, also writes to the ECG History
  */
class WriteLog {

  var writeLogItems: List[WriteLogItem] = List[WriteLogItem]()

  /**
    * Add an item to the WriteLog
    *
    * @param writeLogItem a write log item
    */
  def addItem(writeLogItem: WriteLogItem): Unit = {
    writeLogItems = writeLogItem :: writeLogItems
  }

  /**
    * Get the write log items
    *
    * @param currentTimeVector of type Int
    * @return List[WriteLogItem]
    */
  def partition(currentTimeVector: Int): WriteLog = {
    val writeLog  = new WriteLog
    writeLog.writeLogItems = writeLogItems.filter(item => item.timeVector >= currentTimeVector)

    writeLog
  }

  /**
    * Flush the current writeLog.
    */
  def flush(): Unit = {
    writeLogItems = List[WriteLogItem]()
  }

  /**
    * Retrieves the summed weights of all items in the list of a certain key
    *
    * @param key The key preferred
    * @return The summed weight of all items with the given key
    */
  def getSummedWeightsForKey(key: Char): Int = {
    var sum: Int = 0
    for (writeLogItem <- writeLogItems) {
      if (writeLogItem.operation.key == key) {
        sum += writeLogItem.operation.value
      }
    }
    sum
  }

  /**
    * Retrieves all writeLogItems from a certain key
    *
    * @param key The key
    * @return All writeLogItems for the given key
    */
  def getWriteLogForKey(key: Char) : WriteLog = {
    val writeLog = new WriteLog
    for (writeLogItem <- writeLogItems){
      if (writeLogItem.replicaId == key){
        writeLog.addItem(writeLogItem)
      }
    }
    writeLog
  }
}
