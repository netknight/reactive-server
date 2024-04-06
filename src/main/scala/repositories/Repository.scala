package io.dm
package repositories

import fs2.Stream

trait Repository[F[_], ID, P, E <: BasicEntity[ID, P]] {

  def get(id: ID): F[OpResultEntity[E]]
  def delete(id: ID): F[OpResultAffectedRows]
  def create(payload: P): F[IdObject[ID]]
  def update(id: ID, payload: P): F[OpResultAffectedRows]
  // TODO: Add filtration and pagination
  def list(): Stream[F, E]
}
