package main

import HttpServer.configLayer
import cassandra_quill.Data.{DataService, QueryService}
import zio.{ExitCode, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.Server
import io.getquill._
import io.getquill.ast.Ast
import io.getquill.cassandrazio.Quill
import zio.Console.printLine
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs, Any, Any] = {

    val program = for {
      _ <- HttpServer.startServer
      _ <- ZIO.never
    } yield ExitCode.success

    program
      .provide(
        DataService.live,
        QueryService.live,
        Quill.Cassandra.fromNamingStrategy(SnakeCase),
        Quill.CassandraZioSession.fromPrefix("testStreamDB"),
        configLayer,
        Server.live,
      )
  }
}
