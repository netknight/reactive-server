package io.dm

import cats.effect.{Resource, Sync}
import com.typesafe.config.ConfigFactory
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.module.catseffect.syntax.*
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
  /*
  //import cats.syntax.either.*
  //import cats.syntax.monadError.*
  def load[F[_]](using F: Sync[F]): F[AppConfiguration] =
    F.delay(ConfigSource.default.load[AppConfiguration].leftMap[Throwable](ConfigReaderException[AppConfiguration])).rethrow    
  */
    
  def load[F[_]](configFile: String = "application.conf")(using F: Sync[F]): F[AppConfiguration] =
    ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[F, AppConfiguration]()
    
  def loadResource[F[_]](configFile: String = "application.conf")(using F: Sync[F]): Resource[F, AppConfiguration] =
    Resource.eval(load[F](configFile))
