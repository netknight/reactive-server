package io.dm

import cats.effect.Sync
import cats.syntax.either.*
import cats.syntax.monadError.*

import pureconfig.ConfigReader
import pureconfig.*
import pureconfig.error.ConfigReaderException
import pureconfig.generic.derivation.default.*

final case class DBConfiguration(
  driver: String,
  url: String,
  user: String,
  password: String,
  threadPoolSize: Int
) derives ConfigReader

final case class HttpConfiguration(host: String, port: Int) derives ConfigReader
final case class AppConfiguration(http: HttpConfiguration, db: DBConfiguration) derives ConfigReader


object AppConfiguration:
  def load[F[_]](using F: Sync[F]): F[AppConfiguration] =
    F.delay(ConfigSource.default.load[AppConfiguration].leftMap[Throwable](ConfigReaderException[AppConfiguration])).rethrow
