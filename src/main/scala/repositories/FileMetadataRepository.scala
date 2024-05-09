package io.dm
package repositories

import domain.{FileId, FileMetadata, FileMetadataMutation}

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.{Read, Write}
import doobie.implicits.javatimedrivernative.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import fs2.Stream
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

import java.time.Instant

// TODO: Read this, maybe Quill is a good option to avoid hand-crafted SQL (there is also good structure for Stream-based app): https://github.com/getquill/SBTB2019-Quill-Doobie/blob/master/src/main/scala/service/PeopleTrollsRobotsServiceQuill.scala

class FileMetadataRepository[F[_]: Sync](using L: LoggerFactory[F], tx: Transactor[F]) extends FileMetadataRepository.I[F] {
  given Logger[F] = LoggerFactory.getLogger

  //summon[Read[Account]]
  //summon[Write[Account]]

  // TODO: Check if this can help parsing newtypes: https://github.com/Iltotore/iron/blob/main/doobie/src/io/github/iltotore/iron/doobie.scala
  given Read[FileMetadata] = Read[(String, String, String, Long, Instant, Instant)].map {
      case (id, filename, mimeType, size, created, updated) =>
        FileMetadata(
          id = FileId.applyUnsafe(id),
          created = created,
          updated = updated,
          body = FileMetadataMutation.applyUnsafe(
            filename = filename,
            mimeType = mimeType,
            size = size
          )
        )
  }

  given Write[FileMetadata] = Write[(String, String, String, Long, Instant, Instant)].contramap {
    case FileMetadata(id, created, updated, FileMetadataMutation(filename, mimeType, size)) =>
      (id, filename, mimeType, size, created, updated)
  }

  override def list(): Stream[F, FileMetadata] = {
    // TODO: add logging (how logger is composed with Stream?)
    // debug"Fetching all files" >>
    sql"SELECT id, filename, mime_type, size, created_at, updated_at FROM file_metadata"
      .query[FileMetadata]
      .stream
      .transact(tx)
  }

  override def get(id: FileId): F[OpResultEntity[FileMetadata]] =
    debug"Fetching file with id $id" >>
    sql"SELECT id, filename, mime_type, size, created_at, updated_at FROM file_metadata WHERE id = ${id.asInstanceOf[String]}"
      .query[FileMetadata]
      .option
      .transact(tx)
      // TODO: Find a way to log fetch result
      .map {
        case Some(v) => Right(v)
        case None => Left(NotFoundError)
      }

  override def create(entity: FileMetadata): F[FileMetadata] =
    debug"Creating file: $entity" >>
      // TODO: created/updated should be generated in domain
    sql"INSERT INTO file_metadata (id, filename, mime_type, size, created_at, updated_at) VALUES (${entity.id.asInstanceOf[String]}, ${entity.body.filename.asInstanceOf[String]}, ${entity.body.mimeType.asInstanceOf[String]}, ${entity.body.size.asInstanceOf[Long]}, ${entity.created}, ${entity.updated})"
      .update
      //.withUniqueGeneratedKeys[String]("id") // TODO: Generate in domain
      .run
      .transact(tx)
      .map { _ =>
        entity
        //debug"Created file: $entity with id: $v" >> IdObject(FileId.applyUnsafe(v)).pure[F]
      }

  override def delete(id: FileId): F[OpResultAffectedRows] =
    debug"Deleting file with id $id" >>
    sql"DELETE FROM file_metadata WHERE id = ${id.asInstanceOf[String]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      } // TODO: Add logging of a result

  override def update(entity: FileMetadata): F[OpResultAffectedRows] =
    debug"Updating file: $entity" >>
    sql"UPDATE file_metadata SET filename = ${entity.body.filename.asInstanceOf[String]}, mime_type = ${entity.body.mimeType.asInstanceOf[String]}, size = ${entity.body.size.asInstanceOf[Long]}, updated_at = ${entity.updated} WHERE id = ${entity.id.asInstanceOf[String]}"
      .update
      .run
      .transact(tx)
      .map {
        case 0 => Left(NotFoundError)
        case n => Right(n)
      }
}

object FileMetadataRepository {
  trait I[F[_]: Sync] extends Repository[F, FileId, FileMetadata]
}
