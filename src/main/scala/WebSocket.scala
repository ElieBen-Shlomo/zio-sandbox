import Data.{DataService, User}
import zhttp.http.{Http, Method, Request, Response}
import zhttp.service.ChannelEvent
import zhttp.service.ChannelEvent.UserEvent.{HandshakeComplete, HandshakeTimeout}
import zhttp.service.ChannelEvent.{ChannelRead, ChannelUnregistered, UserEventTriggered}
import zhttp.socket.{WebSocketChannelEvent, WebSocketFrame}
import zio.ZIO
import java.time.Instant

object WebSocket {
  val beginWebsocketConnection: Http[DataService, Throwable, WebSocketChannelEvent, Unit] =
    Http.collectZIO[WebSocketChannelEvent] {

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text(msg))) =>
        ZIO.logInfo(s"Received message $msg") *>
          DataService.insertPerson(User("DB TEST", Instant.now, "message","SessionId", "WAITING")) *>
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
