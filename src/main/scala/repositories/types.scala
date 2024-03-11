package io.dm
package repositories

case class IdObject[T](id: T)

type OpResult[T] = Either[NotFoundError.type, T]
type OpResultAffectedRows = OpResult[Int]
type OpResultEntity[E <: Entity[_]] = OpResult[E]
