package io.dm
package routes

import domain.{AccountId, FileId}

private[this] class PathVar[A](parse: String => Option[A]):
  def unapply(str: String): Option[A] =
    Some(str).filter(_.nonEmpty).flatMap(parse)

object AccountIdVar extends PathVar(s => AccountId.option(s.toLong))
object FileIdVar extends PathVar(FileId.option)