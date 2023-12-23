package io.dm
package routes

import cats.ApplicativeError
import cats.data.{Kleisli, OptionT}
import org.http4s.{HttpRoutes, Request, Response}
import org.typelevel.log4cats.Logger

import cats.implicits._

trait HttpRoutesErrorHandler[F[_], E <: Throwable]:
  def handle(routes: HttpRoutes[F])(using logger: Logger[F]): HttpRoutes[F]

object HttpRoutesErrorHandler:
  def apply[F[_], E <: Throwable](using ev: HttpRoutesErrorHandler[F, E]): HttpRoutesErrorHandler[F, E] = ev
  
  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(handler: E => F[Response[F]])(using ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { (req: Request[F]) =>
      OptionT {
        routes.run(req).value.handleErrorWith { e => handler(e).map(Option(_)) }
      }
    }
    
end HttpRoutesErrorHandler
