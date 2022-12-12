package main

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.*
import zio.http.ChannelEvent.ChannelRead
import zio.http.Middleware.bearerAuth
import zio.http.model.{Method, Status}
import zio.http.socket.{WebSocketChannelEvent, WebSocketFrame}
import zio.http.*

import java.time.Clock
import scala.io.StdIn.readLine
import scala.language.postfixOps

object AuthServer {
  
  val SECRET_KEY = "SECRET"
  implicit val clock: Clock = Clock.systemUTC

  def jwtEncode(username: String): String = {
    val json  = s"""{"user": "${username}"}"""
    val claim = JwtClaim {
      json
    }.issuedNow.expiresIn(300)
    Jwt.encode(claim, SECRET_KEY, JwtAlgorithm.HS512)
  }

  def jwtDecode(token: String): Option[JwtClaim] = {
    Jwt.decode(token, SECRET_KEY, Seq(JwtAlgorithm.HS512)).toOption
  }

  def user: UHttpApp = Http.collect[Request] { case Method.GET -> !! / "user" / name / "greet" =>
    Response.text(s"Welcome to the ZIO party! ${name}")
  } @@ bearerAuth(jwtDecode(_).isDefined)

  def login: UHttpApp = Http.collect[Request] { case Method.GET -> !! / "login" / username / password =>
    if (password.reverse.hashCode == username.hashCode) Response.text(jwtEncode(username))
    else Response.text("Invalid username or password.").setStatus(Status.Unauthorized)
  }

  val jwtRoutes: UHttpApp = login ++ user
}

