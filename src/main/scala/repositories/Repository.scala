package io.dm
package repositories

trait Repository[F[_], ID, A, E <: Entity[ID, A]] {

  def get(id: ID): F[Option[E]]
  def delete(id: ID): F[Boolean]
  def create(a: A): F[E]
  def update(id: ID, a: A): F[E]
  // TODO: Add filtration and pagination
  def list(): F[List[E]]
}
