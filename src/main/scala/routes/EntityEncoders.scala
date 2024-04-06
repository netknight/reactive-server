package io.dm
package routes

import domain.{Account, AccountId, AccountMutation}
import repositories.IdObject

import cats.effect.Concurrent
import fs2.Stream
import io.circe.{Encoder, Json}
import io.circe.generic.auto.{deriveDecoder, deriveEncoder}
import io.github.iltotore.iron.circe.given
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf, streamJsonArrayEncoderOf}

object EntityEncoders:
  object Implicits:
    given jsonEncoder [F[_], A <: Product : Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

    given long [F[_]]: EntityEncoder[F, Long] = jsonEncoderOf[F, Long]
    given idObjectLong [F[_]]: EntityEncoder[F, IdObject[Long]] = jsonEncoderOf[F, IdObject[Long]]
    given accountId [F[_]]: EntityEncoder[F, AccountId] = jsonEncoderOf[F, AccountId]
    given idObjectAccountId [F[_]]: EntityEncoder[F, IdObject[AccountId]] = jsonEncoderOf[F, IdObject[AccountId]]

    // Make a generic solution for any Entity
    given accountEncoder: Encoder[Account] = Encoder.forProduct6("id", "username", "email", "password", "created", "updated")(a =>
      (a.id, a.body.username, a.body.email, a.body.password, a.created, a.updated)
    )
    given account [F[_]]: EntityEncoder[F, Account] = jsonEncoderOf[F, Account]


      /*
      Json = Json.obj(
        "id" -> Json.fromLong(a.id),
      )
      */

    given accountStream [F[_]]: EntityEncoder[F, Stream[F, Account]] = streamJsonArrayEncoderOf[F, Account]

    given accountDecoder[F[_] : Concurrent]: EntityDecoder[F, Account] = accumulatingJsonOf[F, Account]
    given accountMutationDecoder [F[_] : Concurrent]: EntityDecoder[F, AccountMutation] = accumulatingJsonOf[F, AccountMutation]

