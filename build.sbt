name := "mesosphere-test"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++=
  "com.typesafe.akka" %% "akka-actor" % "2.3.9" ::
    "com.typesafe.akka" %% "akka-testkit" % "2.3.9" :: Nil