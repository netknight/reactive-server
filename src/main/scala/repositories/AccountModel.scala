package io.dm
package repositories

import io.github.iltotore.iron.{:|, RefinedTypeOps}
import io.github.iltotore.iron.constraint.all.Positive

import java.time.Instant

type AccountId = Long :| Positive
object AccountId extends RefinedTypeOps[Long, Positive, AccountId]

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

//opaque type AccountEntity = EntityClass[AccountId, AccountMutation]
