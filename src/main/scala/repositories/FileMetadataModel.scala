package io.dm
package repositories

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
  override val body: FileMetadataMutation
) extends BasicEntity[FileMetadataId, FileMetadataMutation]

object FileMetadataEntity {
  /*
  given uuidMeta: Meta[FileMetadataId] =
    Meta[String].imap[FileMetadataId](UUID.fromString)(_.toString)
  */
}

