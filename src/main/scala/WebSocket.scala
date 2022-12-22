package main

import cassandra_quill.Data.{DataService, User}
import zio.http.ChannelEvent.{ChannelRead, ChannelUnregistered, UserEventTriggered}
import zio.http.socket.{WebSocketChannelEvent, WebSocketFrame}
import zio.http.{ChannelEvent, Http}
import zio.ZIO
import zio.http.ChannelEvent.UserEvent.{HandshakeComplete, HandshakeTimeout}

import java.time.Instant

object WebSocket {
  val beginWebsocketConnection: Http[DataService, Throwable, WebSocketChannelEvent, Unit] =
    Http.collectZIO[WebSocketChannelEvent] {

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text(msg))) =>
        ZIO.logInfo(s"Received message $msg") *>
          DataService.insertPerson(User("DB TEST", Instant.now, msg,"SessionId", "WAITING")) *>
          ch.writeAndFlush(WebSocketFrame.text(s"inserted $msg into db"))

      case ChannelEvent(_, UserEventTriggered(event)) =>
        event match {
          case HandshakeComplete => ZIO.logInfo("Connection started!")
          case HandshakeTimeout => ZIO.logInfo("Connection failed!")
        }

      case ChannelEvent(_, ChannelUnregistered) =>
        ZIO.logInfo("Connection closed!")
    }
}
