package io.dm
package service

import repositories.FileMetadataRepository

import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

class FileService[F[_]](using F: Sync[F], L: LoggerFactory[F], R: FileMetadataRepository.I[F]):
  given Logger[F] = LoggerFactory.getLogger

  //def findAll(): F[List[FileMetadata]] = List.empty[FileMetadata].pure

