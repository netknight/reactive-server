package io.dm
package repositories
import java.time.Instant

case class AccountFields(
  username: String,
  email: String,
  password: String
)

/*
case class AccountEntity(
    override val id: Long,
    override val created: Instant,
    override val updated: Instant,
    override val payload: AccountFields
) extends Entity[Long, AccountFields]
*/

case class AccountEntity(
  id: Long,
  username: String,
  email: String,
  password: String                      
  //created: Instant,
  //updated: Instant
)
