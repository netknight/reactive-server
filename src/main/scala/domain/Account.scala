package io.dm
package domain

import cats.{Eq, Show}
import cats.effect.Concurrent

import io.circe.generic.auto.{deriveDecoder, deriveEncoder}
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.autoRefine
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.{Alphanumeric, DescribedAs, Match, MaxLength, MinLength}

import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}


type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

type Email = (Match["^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"] & MaxLength[255]) DescribedAs
  "Value should be an email"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain atleast a letter, a digit and have a length between 6 and 20"

case class Account(username: String :| Username, email: String :| Email, password: String :| Password)


object Account {
  given [F[_]]: EntityEncoder[F, Account] = jsonEncoderOf[F, Account]
  given [F[_]: Concurrent]: EntityDecoder[F, Account] = accumulatingJsonOf[F, Account]

  given Eq[Account] = Eq.fromUniversalEquals
  given Show[Account] = Show.fromToString

  @Deprecated
  val testInstance: Account = Account("test", "test@test.com", "iddQd43")
}

