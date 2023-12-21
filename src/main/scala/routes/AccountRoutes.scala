package io.dm
package routes

import domain.Account
import routes.AccountRoutes.RoutePath
import service.AccountService

import cats.effect.Concurrent
import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.{Logger, LoggerFactory}

class AccountRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] {

  import cats.implicits._

  private val log = LoggerFactory.getLogger
  given Logger[F] = log

  val prefixPath: String = RoutePath.base

  // TODO: Log requests & responses
  val routes: HttpRoutes[F] = H.handle(e => log.error(e)("Account route handling error"))(HttpRoutes.of[F] {
    case GET -> Root =>
      (log.info(s"GET ${RoutePath.base}") >> accountService.getAccountById(0) >>= okOrNotFound)

    case req @ POST -> Root =>
      req.as[Account] >>= { Ok(_) }
  })
}

object AccountRoutes {
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }
}
