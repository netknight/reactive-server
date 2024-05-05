package io.dm
package repositories

import fs2.Stream

trait Repository[F[_], ID, E <: Entity[ID]] {

  def get(id: ID): F[OpResultEntity[E]]
  def delete(id: ID): F[OpResultAffectedRows]
  def create(entity: E): F[E]
  def update(entity: E): F[OpResultAffectedRows]
  // TODO: Add filtration and pagination
  def list(): Stream[F, E]
}
