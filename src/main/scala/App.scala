package io.dm

import routes.{AccountRoutes, DefaultHttpRoutesErrorHandler, HttpRoutesErrorHandler, LifecycleRoute, Route}
import service.AccountService

import cats.effect.{Async, IO, IOApp}
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.{Logger, LoggerFactory}
import org.typelevel.log4cats.slf4j.Slf4jFactory

import cats.implicits.{catsSyntaxApply, catsSyntaxFlatMapOps}
import org.typelevel.log4cats.syntax._

class App[F[_]](using F: Async[F]):
  given LoggerFactory[F] = Slf4jFactory.create[F]
  given AccountService[F] = AccountService()
  given HttpRoutesErrorHandler[F, Throwable] = DefaultHttpRoutesErrorHandler[F]

  given Logger[F] = LoggerFactory.getLogger
  

  private val routes: Seq[Route[F]] =
    Seq(
      LifecycleRoute(),
      AccountRoutes()
    )

  private val app: HttpApp[F] =
    Router(routes.map(r => r.prefixPath -> r.routes): _*).orNotFound

  def start(config: AppConfiguration): F[Unit] =
    info"Starting server with routes: ${routes.map(_.prefixPath).mkString(", ")}" <*
    BlazeServerBuilder[F]
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(app)
      .resource
      .useForever

end App

object App extends IOApp.Simple:
  //import pureconfig.ConfigSource
  //import pureconfig._
  //import pureconfig.generic.derivation.default._

  //private val config: IO[AppConfiguration] = ConfigSource.default.loadOrThrow[AppConfiguration]
  private val config: IO[AppConfiguration] = IO.pure(AppConfiguration(http = HttpConfiguration("localhost", 8080)))
  
  override val run: IO[Unit] = config >>= App[IO].start

