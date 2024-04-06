package io.dm
package domain

import io.github.iltotore.iron.{:|, RefinedTypeOps}
import io.github.iltotore.iron.constraint.all.*

type AccountId = Long :| Positive
object AccountId extends RefinedTypeOps[Long, Positive, AccountId]

type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

//object Username extends RefinedTypeOps[Username]

type Email = (Match["^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"] & MaxLength[255]) DescribedAs
  "Value should be an email"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain at least a letter, a digit and have a length between 6 and 20"
