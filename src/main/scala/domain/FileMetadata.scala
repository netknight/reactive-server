package io.dm
package domain

import cats.{Eq, Show}
import io.circe.Json
import io.circe.generic.auto.{deriveDecoder, deriveEncoder}
import io.github.iltotore.iron.cats.refineValidatedNec
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.{:|, autoRefine}
import io.github.iltotore.iron.constraint.all.*
import org.http4s.EntityEncoder
import org.http4s.circe.{accumulatingJsonOf, jsonEncoderOf, streamJsonArrayEncoderOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import java.time.Instant
import java.util.UUID

//final case class FileId(id: UUID) extends AnyVal
/*
type FileId = (ValidUUID) DescribedAs "The file ID must be a valid UUID"

// TODO: Error text may be generated from the constraint

type Filename = (MinLength[1] & MaxLength[100]) DescribedAs
  "The filename length must be between 1 and 100 characters"

type MimeType = (MinLength[1] & MaxLength[128]) DescribedAs
  "The mime type length must be between 1 and 128 characters"

type FileSize = Positive

// TODO: Find a way to produce FileMetadata from FileMetadataMutation without duplicating the fields

final case class FileMetadataMutation(
  filename: String :| Filename,
  mimeType: String :| MimeType,
  size: Long :| FileSize
)

final case class FileMetadata(
  id: FileId,
  filename: String :| Filename,
  size: Long :| FileSize,
  mimeType: String :| MimeType,
  createdAt: Instant, // TODO: Add past constraint
  updatedAt: Instant // TODO: Add past constraint
)

object FileMetadata {
  given [F[_]]: EntityEncoder[F, FileMetadata] = jsonEncoderOf[F, FileMetadata]
  given [F[_]]: EntityDecoder[F, FileMetadata] = accumulatingJsonOf[F, FileMetadata]
  given [F[_]]: EntityEncoder[F, List[FileMetadata]] = streamJsonArrayEncoderOf[F, FileMetadata]
  
  given Eq[FileMetadata] = Eq.fromUniversalEquals[FileMetadata]
  given Show[FileMetadata] = Show.fromToString[FileMetadata]
}
*/