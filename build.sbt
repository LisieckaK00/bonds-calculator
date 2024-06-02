name := """bonds-calculator"""
organization := "agh.wi"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.4.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "org.scala-lang" %% "toolkit" % "0.1.7"
//libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.9.0"
libraryDependencies += caffeine


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "agh.wi.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "agh.wi.binders._"
