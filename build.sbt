name := "organic-sensor-client"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.github.pureconfig" %% "pureconfig" % "0.14.1",
  "eu.timepit" %% "refined" % "0.9.22",
  "com.fazecast" % "jSerialComm" % "2.6.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)
