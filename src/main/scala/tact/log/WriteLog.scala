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
    * Flush the write log list.
    */
  def flush(): Unit = {
    writeLogItems = List[WriteLogItem]()
  }
}