package tact

import java.rmi.Naming
import java.rmi.server.UnicastRemoteObject

import database.DataBase
import tact.conit.Conit
import tact.manager.ConsistencyManager
import communication.RetrieveLog

class Replica extends UnicastRemoteObject with RetrieveLog {

  Naming.rebind("//localhost:8080/retrieveLog", this)

  /** Each replica has a database, which will be updated by other replicas via the consistency manager **/
  private var dataBase = new DataBase {}

  /** The writelog contains all writes that are made **/
  private var writeLog: List[String] = Nil

  /** Will contain all conits, one for each DB entry **/
  private var conits = Map[Char, Conit]()

  /** The consistency manager will keep track of all error variables in the replica **/
  private var consistencyManager = new ConsistencyManager {}

  /**
    * Retrieves the write log for a remote request
    * @return
    */
  def retrieveLog(): List[String] = {
     writeLog
  }

  /**
    * Request the write log of an remote replica
    * @param address The address of the remote replica
    * @return
    */
  def retrieveLogFromRemote(address: String): List[String] = {
    val rep: Replica = Naming.lookup(address).asInstanceOf[Replica]
    rep.retrieveLog()
  }
}