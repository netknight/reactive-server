package io.dm
package service

import domain.{FileId, FileMetadata, FileMetadataMutation, IdObject}
import repositories.{FileMetadataRepository, NotFoundError, OpResult, OpResultEmpty}

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.functor.*
import cats.syntax.flatMap.*
import fs2.Stream
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

import java.time.Instant

class FileService[F[_]](using F: Sync[F], L: LoggerFactory[F]/*, T: Transactor[F]*/, R: FileMetadataRepository.I[F]):

  given Logger[F] = LoggerFactory.getLogger

  def findAll(): Stream[F, FileMetadata] =
    Stream.eval(info"getFiles()") >> R.list()

  def findById(id: FileId): F[OpResult[FileMetadata]] =
    info"getFileById($id)" >> R.get(id)

  def create(file: FileMetadataMutation): F[IdObject[FileId]] =
    R.create(FileMetadata(FileId.generate, Instant.now, Instant.now, file))
      .map(entity => IdObject(entity.id))

  def update(id: FileId, file: FileMetadataMutation): F[OpResultEmpty] =
    for {
      opResult <- R.get(id)
      result <- opResult match {
        case Right(entity) =>
          R.update(entity.copy(body = file, updated = Instant.now)) map OpResult.mapToEmpty
        case _ =>
          OpResult.notFound.pure
      }
    } yield result

  def delete(id: FileId): F[OpResultEmpty] =
    R.delete(id) map OpResult.mapToEmpty

