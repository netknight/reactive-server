package io.dm
package server

import routes.Route

import cats.effect.{Async, ExitCode, Ref, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import fs2.concurrent.Signal
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.{AutoSlash, CORS, GZip, RequestId, ResponseTiming, Logger as LoggerMiddelware}
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class HttpServer[F[_]](config: HttpConfiguration, httpRoutes: HttpRoutes[F])(using F: Async[F], L: LoggerFactory[F]):
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

  private def serverBuilder(): BlazeServerBuilder[F] =
    BlazeServerBuilder[F]
      .bindHttp(config.port, config.host)
      .withHttpApp(createHttpApp(httpRoutes))
  
  private def logStartup: F[Unit] = 
    info"Starting server at http://${config.host}:${config.port}"
  
  def createServerResource(): Resource[F, Server] =
    for {
      _ <- Resource.eval(logStartup)
      server <- serverBuilder().resource
    } yield server

  def createServerStream(terminationSignal: Signal[F, Boolean]): fs2.Stream[F, ExitCode] =
    for {
      _ <- fs2.Stream.eval(logStartup)
      exitCode <- fs2.Stream.eval(Ref.of(ExitCode.Success))
      server <- serverBuilder().serveWhile(terminationSignal, exitCode)
    } yield server

