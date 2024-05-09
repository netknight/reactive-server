package io.dm

import routes.{DefaultHttpRoutesErrorHandler, HttpRoutesErrorHandler}
import server.HttpServer

import cats.effect.{Async, ExitCode, IO, IOApp, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class App[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]
  given Logger[F] = LoggerFactory.getLogger

  private def createTransactor(config: DBConfiguration): Resource[F, HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[F](config.threadPoolSize)
      transactor <- HikariTransactor.newHikariTransactor(
        config.driver,
        config.url,
        config.user,
        config.password,
        ec
      )
    } yield transactor

  private def createResources(configFile: String): Resource[F, (AppConfiguration, HikariTransactor[F])] =
    for {
      config <- AppConfiguration.loadResource(configFile)
      _ <- Resource.eval(info"Loaded configuration: $config")
      transactor <- createTransactor(config.db)
    } yield (config, transactor)

  private def executeFlywayMigration(transactor: HikariTransactor[F]): fs2.Stream[F, Unit] =
    for {
      _ <- fs2.Stream.eval(info"Launching migration scripts...")
      _ <- fs2.Stream.eval(transactor.configure(ds =>
        F.delay(
          Flyway
            .configure()
            .dataSource(ds)
            .load()
            .migrate()
        )
      ))
    } yield()

  private def launch(config: AppConfiguration, transactor: HikariTransactor[F]): fs2.Stream[F, ExitCode] =
    given Transactor[F] = transactor
    for {
      _ <- executeFlywayMigration(transactor)
      _ <- fs2.Stream.eval(info"Migration complete!")
      exitCode <- HttpServer[F](config).create()
    } yield exitCode


  def create(configFile: String = "application.conf"): fs2.Stream[F, ExitCode] =
    for {
      _ <- fs2.Stream.eval(info"Starting application...")
      exitCode <- fs2.Stream.resource(createResources(configFile)).flatMap((config, tx) => launch(config, tx))
    } yield exitCode

object App extends IOApp.Simple:
  override val run: IO[Unit] =
    App[IO].create().compile.drain

