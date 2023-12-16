package io.dm

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object App extends IOApp {

  implicit val loggerFactory: LoggerFactory[IO] = Slf4jFactory.create[IO]

  override def run(args: List[String]): IO[ExitCode] =
    HttpServer[IO].resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
