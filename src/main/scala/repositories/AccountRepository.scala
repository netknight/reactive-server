package io.dm
package repositories

import cats.effect.Sync
import cats.implicits.toFunctorOps
import cats.syntax.flatMap.*
import cats.syntax.applicative.*
import doobie.implicits.toSqlInterpolator
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}
import fs2.Stream



class AccountRepository[F[_]: Sync](using L: LoggerFactory[F], tx: Transactor[F]) extends AccountRepository.I[F] {

  given Logger[F] = LoggerFactory.getLogger

  def get(id: Long): F[Option[AccountEntity]] =
    debug"Fetching entity with id: $id" >>
    sql"select * from accounts where id = $id"
      .query[AccountEntity]
      //.unique
      .option
      .transact(tx) >>= { v =>
        debug"Fetched entity: $v" >> v.pure[F]
      }
    

  override def delete(id: Long): F[Boolean] = ???
  override def create(entity: AccountFields): F[AccountEntity] =
    sql"insert into accounts(username, email, password) values (${entity.username},${entity.email},${entity.password})"
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .transact(tx) >>= { id =>
        debug"Created entity: [$id] $entity" map { _ => AccountEntity(id, entity.username, entity.email, entity.password) }
      }
  override def update(id: Long, entity: AccountFields): F[AccountEntity] = ???
  override def list(): Stream[F, AccountEntity] =
    sql"select * from accounts"
      .query[AccountEntity]
      .stream
      .transact(tx)
  
}

object AccountRepository {
  trait I[F[_]: Sync] extends Repository[F, Long, AccountFields, AccountEntity]
}
