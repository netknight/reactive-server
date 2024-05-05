package io.dm
package domain

import repositories.BasicEntity

import cats.data.ValidatedNec
import cats.syntax.apply.*
import cats.{Eq, Show}
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.cats.refineValidatedNec
import io.github.iltotore.iron.constraint.all.*

import java.time.Instant

final case class FileMetadataMutation(
  filename: String :| Filename,
  mimeType: String :| MimeType,
  size: Long :| FileSize
)

object FileMetadataMutation {
  given Eq[FileMetadataMutation] = Eq.fromUniversalEquals
  given Show[FileMetadataMutation] = Show.fromToString

  def apply(filename: String, mimeType: String, size: Long): ValidatedNec[String, FileMetadataMutation] =
    (
      filename.refineValidatedNec[Filename],
      mimeType.refineValidatedNec[MimeType],
      size.refineValidatedNec[FileSize]
    ).mapN(FileMetadataMutation.apply)

  def applyUnsafe(filename: String, mimeType: String, size: Long): FileMetadataMutation =
    apply(
      filename = filename, 
      mimeType = mimeType, 
      size = size
    ).fold(e => throw new IllegalArgumentException(e.toString), identity)
}

final case class FileMetadata(
  override val id: FileId,
  override val created: Instant,
  override val updated: Instant,
  override val body: FileMetadataMutation
) extends BasicEntity[FileId, FileMetadataMutation]

object FileMetadata {
  given Eq[FileMetadata] = Eq.fromUniversalEquals
  given Show[FileMetadata] = Show.fromToString
}

/*
object FileMetadata {
  given [F[_]]: EntityEncoder[F, FileMetadata] = jsonEncoderOf[F, FileMetadata]
  given [F[_]]: EntityDecoder[F, FileMetadata] = accumulatingJsonOf[F, FileMetadata]
  given [F[_]]: EntityEncoder[F, List[FileMetadata]] = streamJsonArrayEncoderOf[F, FileMetadata]
}
*/
