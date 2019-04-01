package main.scala.tact.manager

class TimeVector {

  var items: Map[Char, Long] = Map[Char, Long]()

  def getByKey(key: Char): Long = {
    items(key)
  }

  def setByKey(key: Char, timeVector: Long): Unit = {
    if (items(key) < timeVector) {
      items += (key -> timeVector)
    }
  }

}
