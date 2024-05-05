package io.dm
package routes

import domain.AccountMutation
import routes.AccountRoutes.RoutePath
import routes.EntityEncoders.Implicits.Account.given
import service.AccountService

import cats.effect.Concurrent
import cats.syntax.flatMap.*
import cats.syntax.functor.*

//import endpoints4s.{algebra, generic}

import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

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


  //given EntityEncoder[F, IdObject[AccountId]] = jsonEncoderOf[F, IdObject[AccountId]]



  // TODO: Log requests & responses (remove custom logging after)
  override val routes: HttpRoutes[F] = H.handle:
    HttpRoutes.of[F]:
      case GET -> Root =>
        info"GET ${path.base}" >> Ok(accountService.findAll())

      case GET -> Root / AccountIdVar(id) =>
        for {
          _ <- info"GET ${path.base}/$id"
          account <- accountService.findById(id)
          response <- okOrNotFound(account)
        } yield response

      case req @ POST -> Root =>
        for {
          _ <- info"POST ${path.base}"
          account <- req.as[AccountMutation]
          result <- accountService.create(account)
          response <- Created(result)
        } yield response

      case req @ PUT -> Root / AccountIdVar(id) =>
        for {
          _ <- info"PUT ${path.base}/$id"
          account <- req.as[AccountMutation]
          response <- accountService.update(id, account) >>= noContentOrNotFound
        } yield response

      case req @ DELETE -> Root / AccountIdVar(id) =>
        for {
          _ <- info"DELETE ${path.base}/$id"
          response <- accountService.delete(id) >>= noContentOrNotFound
        } yield response

  end routes

end AccountRoutes

object AccountRoutes:
  object RoutePath extends RoutePathObject {
    override def base: String = "/accounts"
  }

end AccountRoutes
