package io.dm
package routes

import cats.Monad
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes, Response}

trait Route[F[_]: Monad] extends Http4sDsl[F]:
  val prefixPath: String
  val routes: HttpRoutes[F]

  // TODO: Make functions below extension methods

  protected def okOrNotFound[A](option: Option[A])(using encoder: EntityEncoder[F, A]): F[Response[F]] =
    option.map(Ok(_)) getOrElse NotFound()

end Route

trait RoutePathObject:
  def base: String
  def buildPath(path: String, paths: String*): String =
    s"$base/$path${paths.mkString("/", "/", "")}"

end RoutePathObject

