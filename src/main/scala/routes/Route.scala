package io.dm
package routes

import cats.Monad
import repositories.OpResult

import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes, Response}

trait Route[F[_]: Monad] extends Http4sDsl[F]:
  val path: RoutePathObject
  val routes: HttpRoutes[F]

  // TODO: Make functions below extension methods

  protected def okOrNotFound[A](result: OpResult[A])(using encoder: EntityEncoder[F, A]): F[Response[F]] =
    result fold (_ => NotFound(), Ok(_))
    //result.map(Ok(_)) getOrElse NotFound()

  protected def noContentOrNotFound(result: OpResult[_]): F[Response[F]] =
    result fold (_ => NotFound(), _ => NoContent())
    //result.map(_ => NoContent()) getOrElse NotFound()

end Route

trait RoutePathObject:
  def base: String
  def buildPath(path: String, paths: String*): String =
    s"$base/$path${paths.mkString("/", "/", "")}"

end RoutePathObject

