package io.dm
package routes

import cats.Monad
import repositories.{IdObject, OpResult}

import io.circe.generic.auto.{deriveDecoder, deriveEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.jsonEncoderOf
import org.http4s.{EntityEncoder, HttpRoutes, Response}

trait Route[F[_]: Monad] extends Http4sDsl[F]:
  val path: RoutePathObject
  val routes: HttpRoutes[F]

  given EntityEncoder[F, Long] = jsonEncoderOf[F, Long]
  given EntityEncoder[F, IdObject[Long]] = jsonEncoderOf[F, IdObject[Long]]

  // TODO: Make functions below extension methods

  protected def okOrNotFound[A](result: OpResult[A])(using encoder: EntityEncoder[F, A]): F[Response[F]] =
    result.map(Ok(_)) getOrElse NotFound()

  protected def noContentOrNotFound(result: OpResult[_]): F[Response[F]] =
    result.map(_ => NoContent()) getOrElse NotFound()

end Route

trait RoutePathObject:
  def base: String
  def buildPath(path: String, paths: String*): String =
    s"$base/$path${paths.mkString("/", "/", "")}"

end RoutePathObject

