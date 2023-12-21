package io.dm
package routes

import cats.implicits.catsSyntaxApply
import cats.MonadError
import org.http4s.{HttpRoutes, InvalidMessageBodyFailure, Response}
import org.http4s.dsl.Http4sDsl

class DefaultHttpRoutesErrorHandler[F[_]](using M: MonadError[F, Throwable]) extends HttpRoutesErrorHandler[F, Throwable] with Http4sDsl[F] {

  val handler: Throwable => F[Response[F]] = {
    case e: InvalidMessageBodyFailure =>
      BadRequest(s"Bad request: ${e.getMessage}")
    case e =>
      InternalServerError("Unhandled error")
  }

  override def handle(alsoOnError: Throwable => F[Unit])(routes: HttpRoutes[F]): HttpRoutes[F] = HttpRoutesErrorHandler(routes) { e =>
    alsoOnError(e) *> handler(e)
  }

}
