package io.dm
package routes

import cats.Monad
import cats.data.NonEmptyList
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.BadRequest
import org.http4s.{HttpRoutes, Response}

trait Route[F[_]: Monad] extends Http4sDsl[F] {

  val prefixPath: String
  val routes: HttpRoutes[F]

  protected def badApiRequest(messages: NonEmptyList[String]): F[Response[F]] =
    BadRequest(Map("messages" -> messages))
}

trait RoutePathObject {
  def base: String
  def buildPath(path: String, paths: String*): String = s"$base/$path${paths.mkString("/", "/", "")}"

}
