package io.dm
package domain

import io.github.iltotore.iron.{:|, RefinedTypeOps}
import io.github.iltotore.iron.constraint.all.*

// -- Account

// TODO: For some reason DescribedAs do not compile in EntityDecoder file
type AccountId = String :| ValidUUID
  //DescribedAs "The Account ID must be a positive number"
object AccountId extends RefinedTypeOps[String, ValidUUID, AccountId] {
  def generate: AccountId = AccountId.applyUnsafe(java.util.UUID.randomUUID().toString)
}

type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

//object Username extends RefinedTypeOps[Username]

type Email = (Match["^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"] & MaxLength[255]) DescribedAs
  "Value should be an email"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain at least a letter, a digit and have a length between 6 and 20"

// --- Files

// TODO: For some reason DescribedAs do not compile in EntityDecoder file
type FileId = (String :| ValidUUID)
  //DescribedAs "The File ID must be a valid UUID"
object FileId extends RefinedTypeOps[String, ValidUUID, FileId] {
  def generate: FileId = FileId.applyUnsafe(java.util.UUID.randomUUID().toString)
}

type Filename = (MinLength[1] & MaxLength[100]) DescribedAs
  "The filename length must be between 1 and 100 characters"

type MimeType = (MinLength[1] & MaxLength[128]) DescribedAs
  "The mime type length must be between 1 and 128 characters"

type FileSize = Positive

// --- Composite objects
case class IdObject[T](id: T)
