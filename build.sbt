name := "TACT conit server"

version := "1.0"

scalaVersion := "2.11.7"

mainClass in (Compile,run) := Some("tact.ReplicaServer")

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11.2"
