package tact.conit

<<<<<<< HEAD:src/main/scala/tact/conit/Conit.scala
class Conit(key: Char, value: Int) {
=======
class Conit(it: Char, va: Int) {

  /** The name of the conit is saved in var item */
  private val item : Char = it
  /** The value of the conit is saved in var value */
  private var value : Int = va
>>>>>>> 4b09ae9b0c9b27d60f8fec8977e945360f718e6f:distributed_systems/src/tact/conit/Conit.scala

  /**
    * Get the value of the current Conit
    *
    * @return an integer representing the value of the current Conit
    */
  def getValue: Int = {
    value
  }

  /**
    * Get the key of the current conit
    *
    * @return a String representing the value of the current conit
    */
  def getKey: Char = {
    key
  }

  /**
    * Updates the value of conit "it" by adding "va"
    *
    * @param it the name of the conit that has te be updated
    * @param va the value that has to be added to the conit
    * @return an option containing an integer representing the new value of the conit if it equals the name of the conit
    *         None if it does not equal the name of the conit
    */
  def update(it: Char, va: Int): Option[Int] = {
    if (key.equals(it)) {
      value += va
      return Some(value)
    }
    None
  }

  /**
    * Updates the value of the current conit by adding "va"
    *
    * @param va the value that has to be added to the conit
    * @return an option containing an integer representing the new value of the conit
    */
  def update(va: Int): Int = {
    value += va
    value
  }
}

