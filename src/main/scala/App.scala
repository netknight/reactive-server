package io.dm

import server.HttpServer

import cats.effect.{IO, IOApp}

object App extends IOApp.Simple:
  override val run: IO[Unit] = HttpServer[IO].create()

