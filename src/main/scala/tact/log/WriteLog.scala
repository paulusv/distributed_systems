package tact.log

/**
  * WriteLog
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
}