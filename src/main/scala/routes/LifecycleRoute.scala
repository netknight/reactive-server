package io.dm
package routes

import routes.LifecycleRoute.RoutePath

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.typelevel.log4cats.{Logger, LoggerFactory}

import cats.implicits.catsSyntaxFlatMapOps
import org.typelevel.log4cats.syntax._

class LifecycleRoute[F[_]](using F: Sync[F], L: LoggerFactory[F]) extends Route[F]:
  given Logger[F] = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F]:
      case GET -> Root / RoutePath.health =>
        info"GET ${RoutePath.buildPath(RoutePath.health)}" >> Ok(LifecycleRoute.okStatus)
  end routes

end LifecycleRoute

object LifecycleRoute:
  object RoutePath extends RoutePathObject:
    override val base = "/server"
    val health = "/health"
  end RoutePath

  private val okStatus = "OK"

end LifecycleRoute
