package io.dm
package routes

import domain.{Account, AccountId, AccountMutation, FileId, FileMetadata, FileMetadataMutation, IdObject}

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

    given jsonEncoderLong [F[_]]: EntityEncoder[F, Long] = jsonEncoderOf[F, Long]
    given jsonEncoderIdObjectLong [F[_]]: EntityEncoder[F, IdObject[Long]] = jsonEncoderOf[F, IdObject[Long]]

    // Account
    given jsonEncoderAccountId [F[_]]: EntityEncoder[F, AccountId] = jsonEncoderOf[F, AccountId]
    given jsonEncoderIdObjectAccountId [F[_]]: EntityEncoder[F, IdObject[AccountId]] = jsonEncoderOf[F, IdObject[AccountId]]

    // Make a generic solution for any Entity
    given encoderAccount: Encoder[Account] = Encoder.forProduct6("id", "username", "email", "password", "created", "updated")(a =>
      (a.id, a.body.username, a.body.email, a.body.password, a.created, a.updated)
    )
    given jsonEncoderAccount [F[_]]: EntityEncoder[F, Account] = jsonEncoderOf[F, Account]

    given streamJsonArrayEncoderAccount [F[_]]: EntityEncoder[F, Stream[F, Account]] = streamJsonArrayEncoderOf[F, Account]
    given jsonDecoderAccountMutation[F[_] : Concurrent]: EntityDecoder[F, AccountMutation] = accumulatingJsonOf[F, AccountMutation]
    given jsonDecoderAccount[F[_] : Concurrent]: EntityDecoder[F, Account] = accumulatingJsonOf[F, Account]

    // FileMetadata

    given jsonEncoderFileMetadataId [F[_]]: EntityEncoder[F, FileId] = jsonEncoderOf[F, FileId]
    given jsonEncoderIdObjectFileMetadataId [F[_]]: EntityEncoder[F, IdObject[FileId]] = jsonEncoderOf[F, IdObject[FileId]]

    given encoderFileMetadata: Encoder[FileMetadata] = Encoder.forProduct6("id", "filename", "mimeType", "size", "created", "updated")(a =>
      (a.id, a.body.filename, a.body.mimeType, a.body.size, a.created, a.updated)
    )

    given jsonEncoderFileMetadata [F[_]]: EntityEncoder[F, FileMetadata] = jsonEncoderOf[F, FileMetadata]

    given streamJsonArrayEncoderFileMetadata [F[_]]: EntityEncoder[F, Stream[F, FileMetadata]] = streamJsonArrayEncoderOf[F, FileMetadata]
    given jsonDecoderFileMetadataMutation[F[_] : Concurrent]: EntityDecoder[F, FileMetadataMutation] = accumulatingJsonOf[F, FileMetadataMutation]
    given jsonDecoderFileMetadata[F[_] : Concurrent]: EntityDecoder[F, FileMetadata] = accumulatingJsonOf[F, FileMetadata]


/*
    Json = Json.obj(
      "id" -> Json.fromLong(a.id),
    )
*/
