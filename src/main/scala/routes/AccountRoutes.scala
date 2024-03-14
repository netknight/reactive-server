package io.dm
package routes

import domain.Account
import routes.AccountRoutes.RoutePath
import service.AccountService

import cats.effect.Concurrent
import cats.syntax.flatMap.*

//import endpoints4s.{algebra, generic}

import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.{Logger, LoggerFactory}

import org.typelevel.log4cats.syntax._

import io.circe.generic.auto.*

class AccountRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] /*with algebra.Endpoints*/:
  given Logger[F] = LoggerFactory.getLogger

  override val path: RoutePath.type = RoutePath

  /*
  // TODO: Implement endpoints4s and generate Swagger documentation
  val findById: Endpoint[Int, Account] =
    endpoint(
      get(path), accountService.findById(1)
    )
   */

  // TODO: Log requests & responses (remove custom logging after)
  override val routes: HttpRoutes[F] = H.handle:
    HttpRoutes.of[F]:
      case GET -> Root =>
        info"GET ${path.base}" >> Ok(accountService.findAll())

      case GET -> Root / LongVar(id) =>
        info"GET ${path.base}/$id" >> accountService.findById(id) >>= okOrNotFound

      case req @ POST -> Root =>
        req.as[Account] >>= { account =>
          // TODO: Return ID here
          info"PUT ${path.base}" >> accountService.createAccount(account) >>= { v => Created(v) }
        }

      case req @ PUT -> Root / LongVar(id) =>
        req.as[Account] >>= { account =>
          info"PUT ${path.base}/$id" >> accountService.updateAccount(id, account) >>= noContentOrNotFound
        }

      case req @ DELETE -> Root / LongVar(id) =>
        info"DELETE ${path.base}/$id" >> accountService.deleteAccount(id) >>= noContentOrNotFound

  end routes

end AccountRoutes

object AccountRoutes:
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }

end AccountRoutes
