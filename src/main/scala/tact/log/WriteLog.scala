package tact.log
import exceptions.MissingKey
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

  def getWriteLogItembyKey(key: Char) : WriteLogItem = {

    for (i <- 0 to writeLogItems.size){
      if (writeLogItems(i).replicaId == key){
        return writeLogItems(i)
      }
    }
    throw MissingKey(key)
  }
}