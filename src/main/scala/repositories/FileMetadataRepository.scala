package io.dm
package repositories

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.{Logger, LoggerFactory}

import java.util.UUID

class FileMetadataRepository[F[_]: Sync](using L: LoggerFactory[F], tx: Transactor[F]) {
  given Logger[F] = LoggerFactory.getLogger
  
  
}

object FileMetadataRepository {
  trait I[F[_]: Sync] extends Repository[F, FileMetadataId, FileMetadataMutation, FileMetadataEntity] 
}
