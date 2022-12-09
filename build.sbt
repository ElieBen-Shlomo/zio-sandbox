ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "untitled"
  )

Compile / run / mainClass := Option("Main")

val zioHttpVersion = "2.0.0-RC7"
libraryDependencies ++= Seq(
  //  "io.monix" %% "monix" % "3.4.0",
  //  "org.typelevel" %% "cats-effect" % "3.4.2",
  "dev.zio" %% "zio" % "2.0.2", // downgrade because of https://github.com/zio/zio-protoquill/issues/217
  "dev.zio" %% "zio-streams" % "2.0.2",
  //  "dev.zio" %% "zio-http" % "0.0.3"
  "io.d11" %% "zhttp" % "2.0.0-RC11",
  // Syncronous JDBC Modules
  "io.getquill" %% "quill-jdbc" % "4.5.0",
  // Or ZIO Modules
  "io.getquill" %% "quill-jdbc-zio" % "4.5.0",
  // Or Postgres Async
  "io.getquill" %% "quill-jasync-postgres" % "4.5.0",
  // Or Cassandra
  "io.getquill" %% "quill-cassandra" % "4.5.0",
  // Or Cassandra + ZIO
  "io.getquill" %% "quill-cassandra-zio" % "4.5.0",
  // Add for Caliban Integration
  "io.getquill" %% "quill-caliban" % "4.5.0",
  "com.github.jwt-scala" %% "jwt-zio-json" % "9.1.2"
)

