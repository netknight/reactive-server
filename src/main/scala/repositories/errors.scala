package io.dm
package repositories

sealed trait RepositoryError

case object NotFoundError extends RepositoryError

