package io.dm

import http.HttpServer

import cats.effect.{Async, ExitCode, IO, IOApp}
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object App extends IOApp {

  given Async[IO] = IO.asyncForIO
  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run(args: List[String]): IO[ExitCode] =
    HttpServer().resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
