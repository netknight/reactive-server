package io.dm
package http.routes

import cats.effect.Concurrent
import org.http4s.HttpRoutes
import org.typelevel.log4cats.Logger

class LifecycleRoutes[F[_]](using F: Concurrent[F]) extends Route[F] {

  val prefixPath: String = "/server"

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "health" => Ok(LifecycleRoutes.okStatus)
  }

}

object LifecycleRoutes {
  private val okStatus = "OK"
}
