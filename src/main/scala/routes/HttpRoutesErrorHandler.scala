package io.dm
package routes

import cats.ApplicativeError
import cats.data.{Kleisli, OptionT}
import org.http4s.{HttpRoutes, Request, Response}

trait HttpRoutesErrorHandler[F[_], E <: Throwable] {
  def handle(alsoOnError: E => F[Unit])(routes: HttpRoutes[F]): HttpRoutes[F]
}

object HttpRoutesErrorHandler {
  def apply[F[_], E <: Throwable](using ev: HttpRoutesErrorHandler[F, E]): HttpRoutesErrorHandler[F, E] = ev

  import cats.implicits._

  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(handler: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { (req: Request[F]) =>
      OptionT {
        routes.run(req).value.handleErrorWith { e => handler(e).map(Option(_)) }
      }
    }
}
