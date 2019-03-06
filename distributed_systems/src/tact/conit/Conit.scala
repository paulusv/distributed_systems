package tact.conit

class Conit(it: Char, va: Int) {

  /** The name of the conit is saved in var item */
  private val item : Char = it;
  /** The value of the conit is saved in var value */
  private var value : Int = va;

  /**
    * Get the value of the current Conit
    *
    * @return an integer representing the value of the current Conit
    */
  def getValue(): Int = {
    value
  }

  /**
    * Get the name of the current conit
    *
    * @return a String representing the value of the current conit
    */
  def getName(): Char = {
    item
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
    if (item.equals(it)) {
      value += va;
      Some(value)
    }
    None
  }

  /**
    * Updates the value of the current conit by adding "va"
    *
    * @param va the value that has to be added to the conit
    * @return an option containing an integer representing the new value of the conit
    */
  def update(va: Int): Option[Int] = {
    value += va;
    Some(value)
  }
}

