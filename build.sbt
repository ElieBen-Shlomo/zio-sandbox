ThisBuild / scalaVersion := "2.13.1"

Compile / run / mainClass := Option("main.Main")

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.4",
  "dev.zio" %% "zio-http" % "0.0.3",
  "io.getquill" %% "quill-jdbc-zio" % "4.6.0",
  "io.getquill" %% "quill-cassandra-zio" % "4.6.0"

)

