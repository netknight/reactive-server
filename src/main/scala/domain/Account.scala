package io.dm
package domain

import repositories.BasicEntity

import cats.data.{Validated, ValidatedNec}
import cats.syntax.apply.*
import cats.{Eq, Show}
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.cats.refineValidatedNec
import io.github.iltotore.iron.constraint.all.*

import java.time.Instant

final case class AccountMutation(
  username: String :| Username,
  email: String :| Email,
  password: String :| Password
)

object AccountMutation {
  given Eq[AccountMutation] = Eq.fromUniversalEquals
  given Show[AccountMutation] = Show.fromToString

  def apply(username: String, email: String, password: String): ValidatedNec[String, AccountMutation] =
    (
      username.refineValidatedNec[Username],
      email.refineValidatedNec[Email],
      password.refineValidatedNec[Password]
    ).mapN(AccountMutation.apply)

  def applyUnsafe(username: String, email: String, password: String): AccountMutation =
    apply(
      username = username,
      email = email,
      password = password
    ).fold(e => throw new IllegalArgumentException(e.toString), identity)
}

final case class Account(
  override val id: AccountId,
  override val created: Instant,
  override val updated: Instant,
  override val body: AccountMutation
) extends BasicEntity[AccountId, AccountMutation]

/*
final case class Account(username: String :| Username, email: String :| Email, password: String :| Password) {
  //def toAccountFields: AccountMutation = toEntityFields(this)
}
*/

object Account {
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

  /*
  @Deprecated
  val testInstance: Account = Account("test", "test@test.com", "iddQd43")

  val toEntityFields: Account => AccountMutation =
    a => AccountMutation(
      username = a.username,
      email = a.email,
      password = a.password
    )

  val fromEntity: AccountEntity => ValidatedNec[String, Account] =
    entity => of(entity.payload.username, entity.payload.email, entity.payload.password)

  def fromEntityF[F[_]: Sync](entity: AccountEntity): F[Account] =
    fromEntity(entity).fold(
      e => Sync[F].raiseError(new Exception(e.toString)),
      v => Sync[F].pure(v)
    )

  extension [F[_]: Sync, G[_]: Applicative: Traverse](v: F[G[AccountEntity]])
    def mapToAccount(): F[G[Account]] =
      v.flatMap(_.traverse(fromEntityF))

  extension [F[_]: Sync](v: Stream[F, AccountEntity])
    def mapToAccount(): Stream[F, Account] =
      v.evalMap(fromEntityF)

   */
}

