package io.dm
package repositories

import cats.effect.Sync
import cats.syntax.flatMap.*
import cats.syntax.applicative.*
import doobie.implicits.toSqlInterpolator
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class AccountRepository[F[_]: Sync](using L: LoggerFactory[F], tx: Transactor[F]) /*extends Repository[F, Long, AccountFields, AccountEntity]*/ {

  given Logger[F] = LoggerFactory.getLogger

  def get(id: Long): F[Option[AccountEntity]] =
    sql"select * from accounts where id = $id"
      .query[AccountEntity]
      //.unique
      .option
      .transact(tx) >>= { v =>
        debug"Fetched entity" >> v.pure[F]
      }
    
  /*
  override def delete(id: Long): F[Boolean] = ???
  override def create(entity: AccountFields): F[AccountEntity] = ???
  override def update(id: Long, entity: AccountFields): F[AccountEntity] = ???
  override def list(): F[List[AccountEntity]] =
    sql"select * from accounts"
      .query[AccountEntity]
      .stream
      .compile
      .toList
      .transact(tx)
  */
  
}
