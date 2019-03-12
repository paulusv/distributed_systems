package exceptions

/**
  * Error thrown when a key is not found in the database
  * @param s The key that is not found
  */
case class MissingKey(s: Char)  extends Exception() {
  "The key " + s + " is not found in the database."
}
