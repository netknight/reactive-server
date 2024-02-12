package io.dm
package repositories

import fs2.Stream

trait Repository[F[_], ID, A, E <: Entity[ID]] {

  def get(id: ID): F[Option[E]]
  def delete(id: ID): F[Boolean]
  def create(a: A): F[E]
  def update(id: ID, a: A): F[E]
  // TODO: Add filtration and pagination
  def list(): Stream[F, E]
}
