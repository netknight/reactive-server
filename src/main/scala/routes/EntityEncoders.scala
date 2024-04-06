package io.dm
package routes

import repositories.{AccountId, IdObject}

import io.circe.generic.auto.deriveEncoder
import io.github.iltotore.iron.circe.given

import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object EntityEncoders:
  object Implicits:
      given long [F[_]]: EntityEncoder[F, Long] = jsonEncoderOf[F, Long]
      given idObjectLong [F[_]]: EntityEncoder[F, IdObject[Long]] = jsonEncoderOf[F, IdObject[Long]]
      given accountId [F[_]]: EntityEncoder[F, AccountId] = jsonEncoderOf[F, AccountId]
      given idObjectAccountId [F[_]]: EntityEncoder[F, IdObject[AccountId]] = jsonEncoderOf[F, IdObject[AccountId]]


