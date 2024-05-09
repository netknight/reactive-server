package io.dm

import db.DatabaseManager
import routes.{DefaultHttpRoutesErrorHandler, HttpRoutesErrorHandler}
import server.HttpServer

import cats.effect.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class App[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]
  given Logger[F] = LoggerFactory.getLogger

  private def createTransactor(config: DBConfiguration): Resource[F, DatabaseManager[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool(config.threadPoolSize)
      tx <- DatabaseManager.transactor(config, ec)
    } yield DatabaseManager(tx)

  private def createResources(configFile: String): Resource[F, (AppConfiguration, DatabaseManager[F])] =
    for {
      config <- AppConfiguration.loadResource(configFile)
      _ <- Resource.eval(info"Loaded configuration: $config")
      transactor <- createTransactor(config.db)
    } yield (config, transactor)

  private def launch(config: AppConfiguration, db: DatabaseManager[F]): fs2.Stream[F, ExitCode] =
    given Transactor[F] = db.transactor
    for {
      _ <- fs2.Stream.eval(info"Launching migration scripts...")
      _ <- fs2.Stream.eval(db.migrate())
      _ <- fs2.Stream.eval(info"Migration complete!")
      exitCode <- HttpServer[F](config).create()
    } yield exitCode


  def create(configFile: String = "application.conf"): fs2.Stream[F, ExitCode] =
    for {
      _ <- fs2.Stream.eval(info"Starting application...")
      exitCode <- fs2.Stream.resource(createResources(configFile)).flatMap((config, db) => launch(config, db))
      _ <- fs2.Stream.eval(info"Application terminated with exit code: $exitCode")
    } yield exitCode

object App extends IOApp.Simple:
  override val run: IO[Unit] =
    App[IO].create().compile.drain

