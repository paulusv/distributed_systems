package tact.log

/**
  * WriteLogItem class.
  *
  * @param timeVector
  * @param replicaId
  * @param operation
  */
class WriteLogItem(var timeVector: Long, var replicaId: Char, var operation: WriteOperation) {

}
