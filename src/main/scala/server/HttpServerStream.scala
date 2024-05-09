package io.dm
package server

import routes.Route

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.{AutoSlash, CORS, GZip, RequestId, ResponseTiming, Logger as LoggerMiddelware}
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class HttpServerStream[F[_]](config: HttpConfiguration, httpRoutes: HttpRoutes[F])(using F: Async[F], L: LoggerFactory[F]):
  given Logger[F] = LoggerFactory.getLogger

  private val createHttpApp: HttpRoutes[F] => HttpApp[F] = { (httpRoutes: HttpRoutes[F]) =>
    AutoSlash(httpRoutes)
  } andThen {
    GZip(_)
  } andThen {
    CORS.policy.withAllowOriginAll(_)
  } andThen {
    RequestId.httpRoutes[F]
  } andThen {
    LoggerMiddelware.httpRoutes[F](
      logHeaders = true,
      logBody = true,
      redactHeadersWhen = _ => false,
      logAction = Some((msg: String) => given_Logger_F.debug(s"[REQUEST LOG]: $msg"))
    )
  } andThen { r =>
    ResponseTiming(r.orNotFound)
  }
  
  private def createServerStream(): fs2.Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(config.port, config.host)
      .withHttpApp(createHttpApp(httpRoutes))
      .serve

  def run(): fs2.Stream[F, ExitCode] =
    createServerStream().evalTap(_ => info"Server started at http://${config.host}:${config.port}")
    
