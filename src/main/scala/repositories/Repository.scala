package io.dm
package repositories

import fs2.Stream

trait Repository[F[_], ID, A, E <: Entity[ID]] {

  def get(id: ID): F[OpResultEntity[E]]
  def delete(id: ID): F[OpResultAffectedRows]
  def create(a: A): F[IdObject[ID]]
  def update(id: ID, a: A): F[OpResultAffectedRows]
  // TODO: Add filtration and pagination
  def list(): Stream[F, E]
}
