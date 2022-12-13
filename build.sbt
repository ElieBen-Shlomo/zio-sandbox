ThisBuild / scalaVersion := "3.2.1"

Compile / run / mainClass := Option("main.Main")

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.2", // https://github.com/zio/zio-protoquill/issues/217
  "dev.zio" %% "zio-http" % "0.0.3",
  "io.getquill" %% "quill-cassandra-zio" % "4.6.0",
  "com.github.jwt-scala" %% "jwt-zio-json" % "9.1.2"
)

