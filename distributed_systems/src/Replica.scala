class  Replica {

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
  var dataBase = new DataBase {}

  /** The writelog contains all writes that are made **/
  var writeLog = Nil

  /** Will contain all conits, one for each DB entry **/
  var conits = Map[Char, Conit] = Map()

  /** The consistency manager will keep track of all error variables in the replica **/
  var consistencyManager = new ConsistencyManager {}
  

}