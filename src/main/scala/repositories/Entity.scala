package io.dm
package repositories

import java.time.Instant


trait Entity[ID, A] {
  val id: ID
  val created: Instant
  val updated: Instant
  val payload: A
}

trait EntityDt[ID, A] extends Entity[ID, A] {
  val created: Instant
  val updated: Instant
}

case class EntityClass[ID, A](id: ID, created: Instant, updated: Instant, payload: A) extends Entity[ID, A]
