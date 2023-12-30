package io.dm
package domain

import repositories.AccountEntity

import cats.data.{Validated, ValidatedNec}
import cats.effect.{Concurrent, Sync}
import cats.{Applicative, Eq, Show, Traverse}
import cats.syntax.apply.*
import cats.syntax.flatMap.*
import cats.syntax.traverse.*
import io.circe.generic.auto.{deriveDecoder, deriveEncoder}
import io.github.iltotore.iron.{:|, autoRefine}
import io.github.iltotore.iron.cats.refineValidatedNec
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}


type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

//object Username extends RefinedTypeOps[Username]

type Email = (Match["^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"] & MaxLength[255]) DescribedAs
  "Value should be an email"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain atleast a letter, a digit and have a length between 6 and 20"

final case class Account(username: String :| Username, email: String :| Email, password: String :| Password)


object Account {
  given [F[_]]: EntityEncoder[F, Account] = jsonEncoderOf[F, Account]
  given [F[_]: Concurrent]: EntityDecoder[F, Account] = accumulatingJsonOf[F, Account]

  given Eq[Account] = Eq.fromUniversalEquals
  given Show[Account] = Show.fromToString

  /*
  def validateTriple(username: String, email: String, password: String): ValidatedNec[String, Account] = {
    val u = username.refineValidatedNec[Username]
    val e = username.refineValidatedNec[Email]
    val p = username.refineValidatedNec[Password]

    //testInstance

    (u, e , p) match {
      case (Validated.Valid(u), Validated.Valid(e), Validated.Valid(p)) => validNec(Account(u, e, p))
      case _ => ???
    }

  }
  */

  def of(username: String, email: String, password: String): ValidatedNec[String, Account] =
    (
      username.refineValidatedNec[Username],
      email.refineValidatedNec[Email],
      password.refineValidatedNec[Password]
    ).mapN(Account.apply)

  
  @Deprecated
  val testInstance: Account = Account("test", "test@test.com", "iddQd43")


  val fromEntity: AccountEntity => ValidatedNec[String, Account] =
    //entity => of(entity.payload.username, entity.payload.email, entity.payload.password)
    entity => of(entity.username, entity.email, entity.password)

  def fromEntityF[F[_]: Sync](entity: AccountEntity): F[Account] =
    fromEntity(entity).fold(
      e => Sync[F].raiseError(new Exception(e.toString)),
      v => Sync[F].pure(v)
    )


  extension [F[_]: Sync, G[_]: Applicative: Traverse](v: F[G[AccountEntity]])
    def mapToAccount(): F[G[Account]] =
      v.flatMap(_.traverse(fromEntityF))

}

