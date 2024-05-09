package io.dm
package server

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.typelevel.log4cats.{Logger, LoggerFactory}
import org.typelevel.log4cats.syntax.LoggerInterpolator
import routes.{AccountRoutes, FileRoutes, HttpRoutesErrorHandler, LifecycleRoute, Route}
import repositories.{AccountRepository, FileMetadataRepository}
import service.{AccountService, FileService}

import doobie.util.transactor.Transactor

class HttpServer[F[_]](config: AppConfiguration)(using F: Async[F], L: LoggerFactory[F], T: Transactor[F], H: HttpRoutesErrorHandler[F, _]):
  given Logger[F] = LoggerFactory.getLogger

  given AccountRepository[F] = AccountRepository()
  given FileMetadataRepository[F] = FileMetadataRepository()

  given AccountService[F] = AccountService()
  given FileService[F] = FileService()

  private def createRouter(routes: Seq[Route[F]]): HttpRoutes[F] =
    Router(routes.map(r => r.path.base -> r.routes): _*)

  def createWithRoutes(routes: Seq[Route[F]]): fs2.Stream[F, ExitCode] =
    for {
      _ <- fs2.Stream.eval(debug"Loaded routes: ${routes.map(_.path.base).mkString(", ")}")
      exitCode <- HttpServerStream[F](config.http, createRouter(routes)).run()
    } yield exitCode


  def create(): fs2.Stream[F, ExitCode] =
    createWithRoutes(
      Seq(
        LifecycleRoute(),
        AccountRoutes(),
        FileRoutes()
      )
    )
