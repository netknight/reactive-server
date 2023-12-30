package io.dm
package repositories

import java.time.Instant

trait Entity[ID, A] {
  val id: ID
  val created: Instant
  val updated: Instant
  val payload: A
}
