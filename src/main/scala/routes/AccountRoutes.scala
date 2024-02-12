package io.dm
package routes

import domain.Account
import routes.AccountRoutes.RoutePath
import service.AccountService

import cats.effect.Concurrent
import cats.syntax.flatMap._

//import endpoints4s.{algebra, generic}

import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.{Logger, LoggerFactory}

import org.typelevel.log4cats.syntax._

class AccountRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] /*with algebra.Endpoints*/:
  given Logger[F] = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  /*
  val findById: Endpoint[Int, Account] =
    endpoint(
      get(path), accountService.findById(1)
    )
   */

  // TODO: Log requests & responses
  val routes: HttpRoutes[F] = H.handle:
    HttpRoutes.of[F]:
      case GET -> Root =>
        info"GET ${RoutePath.base}" >> accountService.findById(1) >>= okOrNotFound

      case req @ POST -> Root =>
        req.as[Account] >>= { Ok(_) }
  end routes

end AccountRoutes

object AccountRoutes:
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }

end AccountRoutes
