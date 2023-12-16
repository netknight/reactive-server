package io.dm

import model.Account

import cats.data.NonEmptyList
import cats.effect.{Async, Resource}
import cats.implicits.catsSyntaxFlatMapOps
import org.http4s.{HttpApp, HttpRoutes, Response}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Server
import io.github.iltotore.iron.autoRefine

class HttpServer[F[_]](implicit F: Async[F]) extends Http4sDsl[F]{

  /*
  def register(account: Account): F[Response[F]] =
    F.println(s"Registered $account"). *> Ok(account)
    //Ok(account)
  */

  private def badApiRequest(messages: NonEmptyList[String]): F[Response[F]] =
    BadRequest(Map("messages" -> messages))

  private val app: HttpApp[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok("Hello")
    case GET -> Root / "accounts" =>
      Ok(Account("test", "test@test.com", "iddQd43"))
    case req @ POST -> Root / "accounts" =>
      val response = req.as[Account] >>= { (req: Account) => Ok(req) }
      F.handleErrorWith(response) { e =>
        badApiRequest(NonEmptyList.of("Bad request"))
      }
  }.orNotFound

  val resource: Resource[F, Server] = {
    BlazeServerBuilder[F]
      .bindHttp(8080)
      .withHttpApp(app)
      .resource
  }
}
