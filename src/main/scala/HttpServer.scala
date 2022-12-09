import Data.DataService
import com.alterationx10.troto.middleware.CustomMiddleware

import scala.io.StdIn.readLine
import zio.*
import zhttp.http.*
import zhttp.http.Method
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.ChannelEvent.UserEvent.{HandshakeComplete, HandshakeTimeout}
import zhttp.service.ChannelEvent.{ChannelRead, ChannelUnregistered, UserEventTriggered}
import zhttp.service.{ChannelEvent, Server}
import zhttp.socket.{WebSocketChannelEvent, WebSocketFrame}
import zio.{Console, ZIOAppDefault}
import zhttp.http.Middleware.bearerAuth

object HttpServer:

  private val getCSRFToken: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case Method.GET -> !! / "security" / "csrf" => Response.text("CSRF token generated for session!")
  } @@ Middleware.csrfGenerate()

  private val protectedRoutes: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      case Method.GET -> !! / "health" => Response.text("Health is good")
    } @@ Middleware.csrfValidate()

  private val authApp: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case Method.GET -> !! / "secret" / "owls" =>
      Response.text("The password is 'Hoot!'")
  } @@ Middleware.basicAuth("admin", "admin")

  private val websocket: Http[DataService, Nothing, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "ws" => WebSocket.beginWebsocketConnection.toSocketApp.toResponse
  }
  
  private val allRoutes: Http[DataService, Nothing, Request, Response] =
//      AuthServer.jwtRoutes <>
      getCSRFToken ++
      websocket ++
      protectedRoutes ++
      authApp
    
  private val routesWithMiddleware: Http[DataService, Throwable, Request, Response] =
    allRoutes @@ Middleware.cors(Cors.config) @@ CustomMiddleware.log

  private val port = 9000

  val startServer: ZIO[DataService, Throwable, Nothing] =
    Console.printLine(s"Starting server on http://localhost:$port") *>
      Server.start[DataService](port, routesWithMiddleware)
    