package io.dm
package routes

import domain.Account
import routes.AccountRoutes.RoutePath
import service.AccountService

import cats.effect.Concurrent
import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.{Logger, LoggerFactory}

import cats.implicits._
import org.typelevel.log4cats.syntax._

class AccountRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] {
  given Logger[F] = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  // TODO: Log requests & responses
  val routes: HttpRoutes[F] = H.handle(HttpRoutes.of[F] {
    case GET -> Root =>
      info"GET ${RoutePath.base}" >> accountService.getAccountById(0) >>= okOrNotFound

    case req @ POST -> Root =>
      req.as[Account] >>= { Ok(_) }
  })
}

object AccountRoutes {
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }
}
