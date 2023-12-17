package io.dm
package http

import http.routes.{AccountRoutes, LifecycleRoutes, Route}
import service.AccountService

import cats.effect.{Async, Resource}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, Response}
import org.typelevel.log4cats.LoggerFactory

class HttpServer[F[_]](using F: Async[F], L: LoggerFactory[F]){

  private val log = LoggerFactory.getLogger

  given AccountService[F] = AccountService()

  private val router = Router(
    Seq(
      LifecycleRoutes(),
      AccountRoutes()
    ).map(r => r.prefixPath -> r.routes): _*
  ).orNotFound


  val resource: Resource[F, Server] = {
    BlazeServerBuilder[F]
      .bindHttp(8080)
      .withHttpApp(router)
      .resource
  }
}
