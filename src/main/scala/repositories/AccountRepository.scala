package io.dm
package repositories

import domain.{Account, AccountId, AccountMutation}

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.*
import doobie.implicits.javatimedrivernative.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import fs2.Stream
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

import java.time.Instant

class AccountRepository[F[_]: Sync](using L: LoggerFactory[F], tx: Transactor[F]) extends AccountRepository.I[F] {

  given Logger[F] = LoggerFactory.getLogger

  //summon[Read[Account]]
  //summon[Write[Account]]
  // TODO: Check if this can help parsing newtypes: https://github.com/Iltotore/iron/blob/main/doobie/src/io/github/iltotore/iron/doobie.scala
  given Read[Account] = Read[(Long, String, String, String, Instant, Instant)].map {
    case (id, username, email, password, created, updated) =>
      Account(AccountId.applyUnsafe(id), created, updated, AccountMutation.applyUnsafe(username, email, password))
  }

  given Write[Account] = Write[(Long, String, String, String, Instant, Instant)].contramap {
    case Account(id, created, updated, AccountMutation(username, email, password)) =>
      (id, username, email, password, created, updated)
  }

  override def list(): Stream[F, Account] =
    // TODO: add logging (how logger is composed with Stream?)
    // debug"Fetching all entities" >>
    sql"select * from accounts"
      .query[Account]
      .stream
      .transact(tx)

  def get(id: AccountId): F[OpResultEntity[Account]] =
    debug"Fetching entity with id: $id" >>
    sql"select * from accounts where id = ${id.asInstanceOf[Long]}"
      .query[Account]
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

  override def create(mutation: AccountMutation): F[IdObject[AccountId]] =
    debug"Creating entity: $mutation" >>
    sql"insert into accounts (username, email, password) values (${mutation.username.asInstanceOf[String]}, ${mutation.email.asInstanceOf[String]}, ${mutation.password.asInstanceOf[String]})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(tx) >>= { v =>
        // TODO: Find better composition of logging and returning value
        debug"Created entity: $mutation with id: $v" >> IdObject(AccountId.applyUnsafe(v)).pure[F]
      }


  override def delete(id: AccountId): F[OpResultAffectedRows] =
    debug"Deleting entity with id: $id" >>
    sql"delete from accounts where id = ${id.asInstanceOf[Long]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging or a result

  override def update(id: AccountId, mutation: AccountMutation): F[OpResultAffectedRows] =
    debug"Updating entity id: $id with $mutation" >>
    sql"update accounts set username = ${mutation.username.asInstanceOf[String]}, email = ${mutation.email.asInstanceOf[String]}, password = ${mutation.password.asInstanceOf[String]} where id = ${id.asInstanceOf[Long]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging or a result

}

object AccountRepository {
  trait I[F[_]: Sync] extends Repository[F, AccountId, AccountMutation, Account]
}
