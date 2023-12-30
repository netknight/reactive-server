package io.dm

import repositories.AccountRepository
import routes.*
import service.AccountService

import cats.effect.{Async, IO, IOApp, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

class App[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]

  given Logger[F] = LoggerFactory.getLogger

  private def databaseSetup(config: DBConfiguration): Resource[F, DatabaseManager[F]] = for {
    ec <- ExecutionContexts.fixedThreadPool[F](32)
    transactor <- DatabaseManager.transactor[F](config, ec)
  } yield DatabaseManager(transactor)

  private def routes(config: AppConfiguration)(transactor: Transactor[F]): F[Seq[Route[F]]] =
    given Transactor[F] = transactor
    given AccountRepository[F] = AccountRepository()
    given AccountService[F] = AccountService()
    F.delay(Seq(
      LifecycleRoute(),
      AccountRoutes()
    ))

  private def startApp(config: AppConfiguration)(routes: Seq[Route[F]]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(Router(routes.map(r => r.prefixPath -> r.routes): _*).orNotFound)
      .resource
      .useForever

  def start(): F[Unit] =
    for {
      _ <- info"Starting app"
      config <- AppConfiguration.load
      _ <- debug"Loaded config: $config"
      _ <- debug"Launching migration scripts..."
      _ <- databaseSetup(config.db).use(_.migrate())
      _ <- debug"Migration complete!"
      routes <- databaseSetup(config.db).use(t => routes(config)(t.transactor))
      _ <- debug"Loaded routes: ${routes.map(_.prefixPath).mkString(", ")}"
      _ <- startApp(config)(routes)
      _ <- info"App terminated"
    } yield ()

end App

object App extends IOApp.Simple:
  override val run: IO[Unit] = App[IO].start()

