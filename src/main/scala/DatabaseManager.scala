package io.dm

import cats.effect.{Async, Resource, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

class DatabaseManager[F[_]: Sync](tx: HikariTransactor[F])(using F: Sync[F]):
  def migrate(): F[Unit] =
    tx.configure(ds =>
      F.delay(
        Flyway
          .configure()
          .dataSource(ds)
          .load()
          .migrate()
      )
    )

  def transactor: HikariTransactor[F] = tx
  
end DatabaseManager

object DatabaseManager:
  def transactor[F[_]: Async](conf: DBConfiguration, ec: ExecutionContext): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor(
      conf.driver,
      conf.url,
      conf.user,
      conf.password,
      ec
    )
