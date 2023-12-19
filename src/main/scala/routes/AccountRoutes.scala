package io.dm
package routes

import domain.Account
import routes.AccountRoutes.RoutePath
import service.AccountService

import cats.data.NonEmptyList
import cats.effect.Concurrent
import cats.implicits.catsSyntaxFlatMapOps
import org.http4s.HttpRoutes
import org.typelevel.log4cats.LoggerFactory

class AccountRoutes[F[_]](using F: Concurrent[F], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] {

  private val log = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  // TODO: Log requests & responses
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      log.info(s"GET ${RoutePath.base}") >> accountService.getAccountById(0) >>= { _.map(Ok(_)).getOrElse(NotFound()) }

    case req @ POST -> Root =>
      F.handleErrorWith(req.as[Account] >>= { Ok(_) }) { e =>
      //F.handleErrorWith(req.as[Account] >>= Ok) { e =>
        // TODO: Proper error handling
        // TODO: Log error
        badApiRequest(NonEmptyList.of("Bad request"))
      }
  }
}

object AccountRoutes {
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }
}
