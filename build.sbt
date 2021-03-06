name := "lettuce-test"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "io.lettuce" % "lettuce-core" % "6.0.2.RELEASE",
  "io.projectreactor" %% "reactor-scala-extensions" % "0.7.0"
)
