package io.dm
package repositories

import doobie.{Read, Write}

import java.time.Instant

// TODO: Find a way how to use summon for Read and Write for AnyVal types
//final case class AccountId(id: Long) extends AnyVal
//type AccountId = Long :| Positive
type AccountId = Long

final case class AccountMutation(
  username: String,
  email: String,
  password: String
)

final case class AccountEntity(
    override val id: AccountId,
    override val created: Instant,
    override val updated: Instant,
    override val payload: AccountMutation
) extends Entity[AccountId, AccountMutation]

// TODO: Find a way how to use polymorphic type EntityClass (need find a way how to summon Read and Write for it)
//type AccountEntity = EntityClass[AccountId, AccountMutation]

object AccountEntity {
  //summon[Read[AccountEntity]]
  //summon[Write[AccountEntity]]
  /*
  given Read[AccountEntity] = Read[(AccountId, String, String, String, Instant, Instant)].map {
    case (id, username, email, password, created, updated) => AccountEntity(id, created, updated, AccountMutation(username, email, password))
  }
  given Write[AccountEntity] = Write[(AccountId, String, String, String, Instant, Instant)].contramap {
    case AccountEntity(id, created, updated, AccountMutation(username, email, password)) => (id, username, email, password, created, updated)
  }
  */
}

/*
case class AccountEntity (
  id: Long,
  username: String,
  email: String,
  password: String,
  //created: Instant,
  //updated: Instant
) extends Entity[Long]//, AccountFields
*/
