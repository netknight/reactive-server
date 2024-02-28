package io.dm
package server

import cats.effect.{Async, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*

import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{RequestId, ResponseTiming, Logger as LoggerMiddelware}
import org.typelevel.log4cats.{Logger, LoggerFactory}
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.syntax.LoggerInterpolator

import routes.{AccountRoutes, DefaultHttpRoutesErrorHandler, HttpRoutesErrorHandler, LifecycleRoute, Route}
import repositories.AccountRepository
import service.AccountService

import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

class HttpServer[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]
  given Logger[F] = LoggerFactory.getLogger

  private case class ServerResources(transactor: HikariTransactor[F], configuration: AppConfiguration)

  private def createResources(configFile: String): Resource[F, ServerResources] =
    for {
      config <- AppConfiguration.loadResource(configFile)
      ec <- ExecutionContexts.fixedThreadPool[F](config.db.threadPoolSize)
      transactor <- DatabaseManager.transactor[F](config.db, ec)
    } yield ServerResources(transactor, config)

  private def createRoutes(resources: ServerResources): F[Seq[Route[F]]] =
    given Transactor[F] = resources.transactor
    given AccountRepository[F] = AccountRepository()
    given AccountService[F] = AccountService()
    F.delay(Seq(
      LifecycleRoute(),
      AccountRoutes()
    ))

  private def createRouter(routes: Seq[Route[F]]): HttpRoutes[F] =
    Router(routes.map(r => r.prefixPath -> r.routes): _*)

  private def createRequestLogMiddleware(httpRoutes: HttpRoutes[F]) =
    LoggerMiddelware.httpRoutes[F](
      logHeaders = true,
      logBody = true,
      redactHeadersWhen = _ => false,
      logAction = Some((msg: String) => given_Logger_F.debug(s"[REQUEST LOG]: $msg"))
    )(httpRoutes).orNotFound

  private def createRequestIdMiddleware(httpRoutes: HttpRoutes[F]) =
    RequestId.httpRoutes[F](httpRoutes)

  private def bindHttpServer(config: AppConfiguration)(routes: Seq[Route[F]]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      // TODO: Find a way how to chain middlewares correctly
      .withHttpApp(ResponseTiming(createRequestLogMiddleware(createRequestIdMiddleware(createRouter(routes)))))
      .resource
      .useForever

  private def create(resources: ServerResources): F[Unit] =
    for {
      _ <- debug"Loaded config: ${resources.configuration}"
      db = DatabaseManager(resources.transactor)
      _ <- debug"Launching migration scripts..."
      _ <- db.migrate()
      _ <- debug"Migration complete!"
      routes <- createRoutes(resources)
      _ <- debug"Loaded routes: ${routes.map(_.prefixPath).mkString(", ")}"
      _ <- bindHttpServer(resources.configuration)(routes)
    } yield ()

  def create(configFile: String = "application.conf"): F[Unit] =
    for {
      _ <- info"Starting app..."
      _ <- createResources(configFile).use(create)
      _ <- info"App stopped"
    } yield ()
