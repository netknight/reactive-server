package io.dm
package repositories

import domain.{Account, AccountId, AccountMutation}

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.{Read, Write}
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
  given Read[Account] = Read[(String, String, String, String, Instant, Instant)].map {
    case (id, username, email, password, created, updated) =>
      Account(
        id = AccountId.applyUnsafe(id),
        created = created,
        updated = updated,
        body = AccountMutation.applyUnsafe(
          username = username,
          email = email,
          password = password
        )
      )
  }

  given Write[Account] = Write[(String, String, String, String, Instant, Instant)].contramap {
    case Account(id, created, updated, AccountMutation(username, email, password)) =>
      (id, username, email, password, created, updated)
  }

  override def list(): Stream[F, Account] =
    // TODO: add logging (how logger is composed with Stream?)
    // debug"Fetching all accounts" >>
    sql"SELECT id, username, email, password, created_at, updated_at FROM accounts"
      .query[Account]
      .stream
      .transact(tx)

  override def get(id: AccountId): F[OpResultEntity[Account]] =
    debug"Fetching account with id: $id" >>
    sql"SELECT id, username, email, password, created_at, updated_at FROM accounts WHERE id = ${id.asInstanceOf[String]}"
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

  override def create(entity: Account): F[Account] =
    debug"Creating account: $entity" >>
    // TODO: created/updated should be generated in domain
    sql"INSERT INTO accounts (id, username, email, password, created_at, updated_at) VALUES (${entity.id.asInstanceOf[String]}, ${entity.body.username.asInstanceOf[String]}, ${entity.body.email.asInstanceOf[String]}, ${entity.body.password.asInstanceOf[String]}, ${entity.created}, ${entity.updated})"
      .update
      //.withUniqueGeneratedKeys[Long]("id")
      .run
      .transact(tx)
      .map { _ =>
        entity
        // TODO: Find better composition of logging and returning value
        //debug"Created account: $entity with id: $v" >> IdObject(AccountId.applyUnsafe(v)).pure[F]
      }


  override def delete(id: AccountId): F[OpResultAffectedRows] =
    debug"Deleting account with id: $id" >>
    sql"DELETE FROM accounts WHERE id = ${id.asInstanceOf[String]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging of a result

  override def update(entity: Account): F[OpResultAffectedRows] =
    debug"Updating account: $entity" >>
    sql"UPDATE accounts SET username = ${entity.body.username.asInstanceOf[String]}, email = ${entity.body.email.asInstanceOf[String]}, password = ${entity.body.password.asInstanceOf[String]}, updated_at = ${entity.updated} WHERE id = ${entity.id.asInstanceOf[String]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging or a result

}

object AccountRepository {
  trait I[F[_]: Sync] extends Repository[F, AccountId, Account]
}
