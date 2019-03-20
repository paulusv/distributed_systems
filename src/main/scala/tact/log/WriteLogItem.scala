package tact.log

/**
  * WriteLogItem class.
  *
  * @param timeVector of type Long
  * @param replicaId  of type Char
  * @param operation  of type WriteOperation
  */
class WriteLogItem(var timeVector: Long, var replicaId: Char, var operation: WriteOperation) {

}
