package main

import cassandra_quill.Data
import cassandra_quill.Data.DataService
import zio.{Console, ZIO, ZLayer}
import zio.http.model.Method
import zio.http.{Http, Request, Response, Server, ServerConfig}
import zio.http._

import java.io.IOException

object HttpServer {

  private val websocket: Http[DataService, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "ws" => WebSocket.beginWebsocketConnection.toSocketApp.toResponse
  }

  private val port = 9000

  val configLayer: ZLayer[Any, Nothing, ServerConfig] = ServerConfig
    .live(ServerConfig.default.port(port))

  val startServer: ZIO[DataService with Server, IOException, Nothing] =
    Console.printLine(s"Starting server on http://localhost:$port") *>
      Server.serve(websocket)

}
