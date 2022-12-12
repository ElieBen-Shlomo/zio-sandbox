package main

import HttpServer.{configLayer, port}
import zio.*
import io.getquill.cassandrazio.Quill.CassandraZioSession
import zio.ZLayer
import zio.*
import io.getquill.*
import io.getquill.cassandrazio.Quill
import Data.{DataService, QueryService, User}
import zio.Console.printLine
import zio.http.Server

object Main extends ZIOAppDefault:

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    val program = for {
      _ <- HttpServer.startServer
      _ <- ZIO.never
    } yield ExitCode.success

    program
      .provide(
        Quill.CassandraZioSession.fromPrefix("testStreamDB"),
        Quill.Cassandra.fromNamingStrategy(Literal),
        QueryService.live,
        DataService.live,
        configLayer,
        Server.live
      )
    