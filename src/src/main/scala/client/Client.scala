package main.scala.client

import java.rmi.Naming
import java.time.LocalDateTime

import main.scala.tact.Tact

object Client {

  /**
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val rmiServer = args(0)
    val replicaId = args(1)
    val readOrWrite = args(2)
    val key = args(3).toCharArray()(0)

    val server = Naming.lookup("//" + rmiServer + "/" + replicaId) match {
      case s: Tact => s
      case other => throw new RuntimeException("Wrong objesct: " + other)
    }

    if (readOrWrite == "read") {
      val start = System.currentTimeMillis()
      val value = server.read(key)
      val latency = System.currentTimeMillis() - start

      println("[" + LocalDateTime.now().toString + "] [INFO] Read key = " + key + " and value = " + value + " in " + latency + "ms")
    } else if (readOrWrite == "write") {
      val start = System.currentTimeMillis()
      val value = args(4).toInt
      server.write(key, value)
      val latency = System.currentTimeMillis() - start

      println("[" + LocalDateTime.now().toString + "] [INFO] Write key = " + key + " and value = " + value + " in " + latency + "ms")
    }
  }
}
