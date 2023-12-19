package io.dm
package routes

import routes.LifecycleRoute.RoutePath

import cats.effect.Sync
import cats.implicits.catsSyntaxFlatMapOps
import org.http4s.HttpRoutes
import org.typelevel.log4cats.LoggerFactory

class LifecycleRoute[F[_]](using F: Sync[F], L: LoggerFactory[F]) extends Route[F] {
  
  private val log = LoggerFactory.getLogger

  val prefixPath: String = RoutePath.base

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / RoutePath.health => 
      log.info(s"GET ${RoutePath.buildPath(RoutePath.health)}") >> Ok(LifecycleRoute.okStatus)
  }
}

object LifecycleRoute {
  object RoutePath extends RoutePathObject {
    override val base = "/server"
    val health = "/health"
  }
  private val okStatus = "OK"
}
