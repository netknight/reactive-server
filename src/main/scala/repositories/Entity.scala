package io.dm
package repositories

import java.time.Instant


trait Entity[ID] {
  val id: ID
}

trait EntityWithCreatedUpdated[ID] extends Entity[ID] {
  val created: Instant
  val updated: Instant
}

trait EntityWithBody[ID, A] extends Entity[ID] {
  val body: A
}

trait BasicEntity[ID, A] extends EntityWithCreatedUpdated[ID], EntityWithBody[ID, A]

case class EntityClass[ID, A](
   id: ID,
   created: Instant,
   updated: Instant,
   body: A
) extends BasicEntity[ID, A]
