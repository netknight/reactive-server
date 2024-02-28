package io.dm
package server

import cats.effect.{Async, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*

import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

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

// TODO Refactor this class to multiple structures: App, HttpServer & Db/Config initializer (too much logic for a class) example: https://github.com/jaspervz/todo-http4s-doobie/blob/master/src/main/scala/HttpServer.scala

class HttpServer[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]
  given Logger[F] = LoggerFactory.getLogger

  private type DbManager = DatabaseManager[F]
  private type DbResource = Resource[F, DbManager]

  private def databaseResource(config: DBConfiguration): DbResource =
    for {
      ec <- ExecutionContexts.fixedThreadPool[F](config.threadPoolSize)
      transactor <- DatabaseManager.transactor[F](config, ec)
    } yield DatabaseManager(transactor)

  private def createRoutes(config: AppConfiguration)(transactor: Transactor[F]): F[Seq[Route[F]]] =
    given Transactor[F] = transactor

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

  // TODO: Find a way how to chain middlewares
  private def createRequestIdMiddleware(httpRoutes: HttpRoutes[F]) =
    RequestId.httpRoutes[F](httpRoutes)

  private def startApp(config: AppConfiguration)(routes: Seq[Route[F]]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(ResponseTiming(createRequestLogMiddleware(createRequestIdMiddleware(createRouter(routes)))))
      .resource
      .useForever

  private def create(config: AppConfiguration, db: DbManager): F[Unit] =
    for {
      _ <- debug"Launching migration scripts..."
      _ <- db.migrate()
      _ <- debug"Migration complete!"
      // TODO: Remove this. It is just for test purposes.
      /*
      _ <- {
        given Transactor[F] = db.transactor
        AccountRepository().get(1)
      }
      */
      routes <- createRoutes(config)(db.transactor)
      _ <- debug"Loaded routes: ${routes.map(_.prefixPath).mkString(", ")}"
      _ <- startApp(config)(routes)
      _ <- info"App started"
    } yield ()

  /*
  def create(configFile: String = "application.conf"): F[Unit] =
    for {
      config <- AppConfiguration.load
      _ <- debug"Loaded config: $config"

    }
  */

  def start(): F[Unit] =
    for {
      _ <- info"Starting app"
      config <- AppConfiguration.load
      _ <- debug"Loaded config: $config"
      _ <- databaseResource(config.db).use { db => create(config, db) }
      _ <- info"App terminated"
      /*
      _ <- debug"Launching migration scripts..."
      db = databaseResource(config.db)
      _ <- db.use(_.migrate())
      _ <- debug"Migration complete!"
      // TODO: Remove this. It is just for test purposes.
      _ <- db.use { t =>
        given Transactor[F] = t.transactor
        AccountRepository().get(1)
      }
      routes <- db.use(t => createRoutes(config)(t.transactor))
      _ <- debug"Loaded routes: ${routes.map(_.prefixPath).mkString(", ")}"
      _ <- startApp(config)(routes)
      _ <- info"App terminated"
      */
    } yield {
      databaseResource(config.db).use { db =>
        create(config, db)
      }
    }


