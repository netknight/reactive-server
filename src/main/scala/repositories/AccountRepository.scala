package io.dm
package repositories

import cats.effect.Sync
import cats.syntax.functor.*
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

  override def list(): Stream[F, AccountEntity] =
    // TODO: add logging (how logger is composed with Stream?)
    // debug"Fetching all entities" >>
    sql"select * from accounts"
      .query[AccountEntity]
      .stream
      .transact(tx)

  // TODO: Change Option to Either
  def get(id: Long): F[OpResultEntity[AccountEntity]] =
    debug"Fetching entity with id: $id" >>
    sql"select * from accounts where id = $id"
      .query[AccountEntity]
      //.unique
      .option
      .transact(tx)
      .map {
        case Some(v) => Right(v)
        case None => Left(NotFoundError)
      }
      // TODO: Add logging
      /*
      >>= { v =>
        // TODO: Find better composition of logging and returning value
        debug"Fetched entity: $v" >> v.pure[F]
      }
    */

  override def create(entity: AccountFields): F[IdObject[Long]] =
    debug"Creating entity: $entity" >>
    sql"insert into accounts (username, email, password) values (${entity.username}, ${entity.email}, ${entity.password})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(tx) >>= { v =>
        // TODO: Find better composition of logging and returning value
        debug"Created entity: $entity with id: $v" >> IdObject(v).pure[F]
      }


  override def delete(id: Long): F[OpResultAffectedRows] =
    debug"Deleting entity with id: $id" >>
    sql"delete from accounts where id = $id"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging or a result

  override def update(id: Long, entity: AccountFields): F[OpResultAffectedRows] =
    debug"Updating entity id: $id with $entity" >>
    sql"update accounts set username = ${entity.username}, email = ${entity.email}, password = ${entity.password} where id = $id"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging or a result

}

object AccountRepository {
  trait I[F[_]: Sync] extends Repository[F, Long, AccountFields, AccountEntity]
}
