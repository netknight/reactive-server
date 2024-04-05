package io.dm
package repositories

import doobie.{Meta, Read, Write}

import java.time.Instant
import java.util.UUID

type FileMetadataId = UUID

final case class FileMetadataMutation(
  filename: String,
  mimeType: String,
  size: Long,
)

final case class FileMetadataEntity(
  override val id: FileMetadataId,
  override val created: Instant,
  override val updated: Instant,
  override val payload: FileMetadataMutation
) extends Entity[FileMetadataId, FileMetadataMutation]

object FileMetadataEntity {
  given uuidMeta: Meta[FileMetadataId] =
    Meta[String].imap[FileMetadataId](UUID.fromString)(_.toString)
  summon[Read[FileMetadataEntity]]
  summon[Write[FileMetadataEntity]]
}

