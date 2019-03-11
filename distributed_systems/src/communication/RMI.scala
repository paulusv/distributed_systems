package communication

import java.rmi._

/**
  * Remote Method Invocation interface
  */
trait RMI extends Remote {
  @throws[RemoteException]
  def add(x: Int, y: Int): Int
}