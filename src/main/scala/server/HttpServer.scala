package io.dm
package server

import cats.effect.{Async, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{AutoSlash, CORS, RequestId, ResponseTiming, Logger as LoggerMiddelware}
import org.typelevel.log4cats.{Logger, LoggerFactory}
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.syntax.LoggerInterpolator
import routes.{AccountRoutes, DefaultHttpRoutesErrorHandler, HttpRoutesErrorHandler, LifecycleRoute, Route}
import repositories.AccountRepository
import service.AccountService

import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

class HttpServer[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]
  given Logger[F] = LoggerFactory.getLogger

  private def createTransactor(config: DBConfiguration): Resource[F, HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[F](config.threadPoolSize)
      transactor <- HikariDatabaseManager.transactor[F](config, ec)
    } yield transactor

  private def createRoutes(config: AppConfiguration)(using transactor: HikariTransactor[F]): F[Seq[Route[F]]] =
    given AccountRepository[F] = AccountRepository()
    given AccountService[F] = AccountService()
    F.delay(Seq(
      LifecycleRoute(),
      AccountRoutes()
    ))

  private def createRouter(routes: Seq[Route[F]]): HttpRoutes[F] =
    Router(routes.map(r => r.path.base -> r.routes): _*)

  private val createHttpApp: HttpRoutes[F] => HttpApp[F] = { (httpRoutes: HttpRoutes[F]) =>
    AutoSlash(httpRoutes)
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

  private def bindHttpServer(config: AppConfiguration)(routes: Seq[Route[F]]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(createHttpApp(createRouter(routes)))
      .resource
      .useForever

  private def create(config: AppConfiguration)(using transactor: HikariTransactor[F]): F[Unit] =
    for {
      _ <- debug"Launching migration scripts..."
      _ <- HikariDatabaseManager(transactor).migrate()
      _ <- debug"Migration complete!"
      routes <- createRoutes(config)
      _ <- debug"Loaded routes: ${routes.map(_.path.base).mkString(", ")}"
      _ <- bindHttpServer(config)(routes)
    } yield ()

  def create(configFile: String = "application.conf"): F[Unit] =
    for {
      _ <- info"Starting app..."
      config <- AppConfiguration.load(configFile)
      _ <- info"Loaded config: $config"
      _ <- createTransactor(config.db).use(create(config)(using _))
      _ <- info"App stopped"
    } yield ()
