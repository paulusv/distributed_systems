package tact.log

/**
  * WriteLogItem class.
  *
  * @param timeVector
  * @param replicaId
  * @param operation
  */
case class WriteLogItem(timeVector: Long, replicaId: Char, operation: WriteOperation) {

}
