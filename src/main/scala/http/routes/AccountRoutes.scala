package io.dm
package http.routes

import domain.Account
import service.AccountService

import cats.data.NonEmptyList
import cats.effect.Concurrent
import cats.implicits.catsSyntaxFlatMapOps
import org.http4s.HttpRoutes
import org.typelevel.log4cats.LoggerFactory


class AccountRoutes[F[_]](using F: Concurrent[F], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] {

  private val log = LoggerFactory.getLogger

  val prefixPath: String = "/accounts"

  // TODO: Log requests & responses
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      log.info("GET /accounts") >> accountService.getAccountById(0) >>= { acc => acc.map(Ok(_)).getOrElse(NotFound()) }

      //log.info("GET /accounts") >> accountService.getAccountById(0)  Ok(_) }
    case req @ POST -> Root =>
      F.handleErrorWith(req.as[Account] >>= { (req: Account) => Ok(req) }) { e =>
      //F.handleErrorWith(req.as[Account] >> Ok(req)) { e =>
        // TODO: Proper error handling
        // TODO: Log error
        badApiRequest(NonEmptyList.of("Bad request"))
      }
  }
}
