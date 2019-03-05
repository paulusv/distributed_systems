package tact.conit

class Conit(it: String, va: Int) {

  /** The name of the conit is saved in var item */
  private var item = it;
  /** The value of the conit is saved in var value */
  private var value = va;

  /**
    * Get the value of the current Conit
    * @return an integer representing the value of the current Conit
    */
  def getValue(): Int = {
    value
  }

  /**
    * Get the name of the current conit
    * @return a String representing the value of the current conit
    */
  def getName(): String = {
    item
  }

  /**
    * Updates the value of conit "it" by adding "va"
    * @param it the name of the conit that has te be updated
    * @param va the value that has to be added to the conit
    * @return an integer representing the new value of the conit
    */
  def update(it: String, va: Int): Int ={
    if(item.equals(it)){
      value += va;
    }
    value
  }
}
