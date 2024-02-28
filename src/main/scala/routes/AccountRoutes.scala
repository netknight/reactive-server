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

import io.circe.generic.auto._

// TODO: Refactor list requests to Streams(fs2), like in example here: https://github.com/jaspervz/todo-http4s-doobie/blob/master/src/main/scala/HttpServer.scala

class AccountRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val accountService: AccountService[F]) extends Route[F] /*with algebra.Endpoints*/:
  given Logger[F] = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  /*
  val findById: Endpoint[Int, Account] =
    endpoint(
      get(path), accountService.findById(1)
    )
   */


  // TODO: Log requests & responses (remove custom logging after)
  val routes: HttpRoutes[F] = H.handle:
    HttpRoutes.of[F]:
      case GET -> Root =>
        // TODO: Use list service method
        info"GET ${RoutePath.base}" >> accountService.findById(1) >>= okOrNotFound

      case GET -> Root / LongVar(id) =>
        info"GET ${RoutePath.base}/$id" >> accountService.findById(id) >>= okOrNotFound

      case req @ POST -> Root =>
        req.as[Account] >>= { account =>
          // TODO: Return ID here
          info"PUT ${RoutePath.base}" >> accountService.createAccount(account) >>= { v => Created(/*v.json*/) }
        }

      case req @ PUT -> Root / LongVar(id) =>
        req.as[Account] >>= { account =>
          info"PUT ${RoutePath.base}/$id" >> accountService.updateAccount(id, account) >>= noContentOrNotFound
        }

      case req @ DELETE -> Root / LongVar(id) =>
        info"DELETE ${RoutePath.base}/$id" >> accountService.deleteAccount(id) >>= noContentOrNotFound

  end routes

end AccountRoutes

object AccountRoutes:
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }

end AccountRoutes
