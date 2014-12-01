name := "Simple web crawler"

version := "0.1"

scalaVersion := "2.11.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"

libraryDependencies += "com.hazelcast" % "hazelcast" % "3.3.3"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.1"
