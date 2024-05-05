package io.dm
package routes

import routes.LifecycleRoute.RoutePath

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}
import cats.syntax.flatMap.*


class LifecycleRoute[F[_]](using F: Sync[F], L: LoggerFactory[F]) extends Route[F]:
  given Logger[F] = LoggerFactory.getLogger

  override val path: RoutePath.type = RoutePath

  override val routes: HttpRoutes[F] =
    HttpRoutes.of[F]:
      case GET -> Root / path.health =>
        info"GET ${path.buildPath(path.health)}" >> Ok(LifecycleRoute.okStatus)
  end routes

end LifecycleRoute

object LifecycleRoute:
  object RoutePath extends RoutePathObject:
    override val base = "/server"
    val health = "/health"
  end RoutePath

  private val okStatus = "OK"

end LifecycleRoute
