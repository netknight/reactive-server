package io.dm
package routes

import cats.MonadError
import cats.syntax.flatMap.*
import org.http4s.{HttpRoutes, InvalidMessageBodyFailure, Response}
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger

class DefaultHttpRoutesErrorHandler[F[_]](using M: MonadError[F, Throwable]) extends HttpRoutesErrorHandler[F, Throwable] with Http4sDsl[F] {

  val handler: Throwable => F[Response[F]] = {
    case e: InvalidMessageBodyFailure =>
      BadRequest(s"Bad request: ${e.getMessage}")
    case e =>
      InternalServerError("Unhandled error")
  }

  override def handle(routes: HttpRoutes[F])(using logger: Logger[F]): HttpRoutes[F] = HttpRoutesErrorHandler(routes) { e =>
    logger.error(e)(e.getMessage) >> handler(e)
  }

}
