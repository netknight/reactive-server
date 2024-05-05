package io.dm
package repositories

import cats.Eval

//case class IdObject[T](id: T)

type OpResult[T] = Either[NotFoundError.type, T]
type OpResultEmpty = OpResult[Unit]
type OpResultAffectedRows = OpResult[Int]
type OpResultEntity[E <: Entity[_]] = OpResult[E]

object OpResult {
  def successful[T](t: T): OpResult[T] = Right(t)
  def notFound[T]: OpResult[T] = Left(NotFoundError)

  def mapToEmpty[T](r: OpResult[T]): OpResultEmpty = r.map(_ => ())
  
  extension [T](r: Option[T])
    def toOpResult: OpResult[T] = r.toRight(NotFoundError)

  extension [T](r: Eval[Boolean])
    def toOpResultAffectedRows: OpResultAffectedRows =
      if r.value then Right(1) else Left(NotFoundError)
}

