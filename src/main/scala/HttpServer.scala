package main

import scala.io.StdIn.readLine
import zio.*
import zio.{Console, ZIOAppDefault}
import zio.*
import zio.http.ChannelEvent.{ChannelRead, ChannelUnregistered, UserEventTriggered}
import zio.http.*
import zio.http.model.Method
import zio.http.socket.{WebSocketChannelEvent, WebSocketFrame}
import zio.http.ChannelEvent.UserEvent.*

import Data.DataService

object HttpServer:

  private val websocket: Http[DataService, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "ws" => WebSocket.beginWebsocketConnection.toSocketApp.toResponse
  }
  
  private val allRoutes: Http[DataService, Nothing, Request, Response] =
      AuthServer.jwtRoutes ++ websocket

  private val port = 9000

  val configLayer: ZLayer[Any, Nothing, ServerConfig] = ServerConfig
      .live(ServerConfig.default.port(port))

  val startServer =
    Console.printLine(s"Starting server on http://localhost:$port") *>
      Server.serve(allRoutes)
