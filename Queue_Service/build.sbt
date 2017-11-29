name := "qs"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.7"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.7"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"