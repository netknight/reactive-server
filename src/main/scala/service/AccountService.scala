package io.dm
package service

import domain.Account

import cats.data.Kleisli
import cats.effect.Sync
import cats.effect.std.Random
import org.typelevel.log4cats.{Logger, LoggerFactory}
import org.typelevel.log4cats.syntax.*
import cats.implicits.catsSyntaxFlatMapOps
import doobie.util.transactor.Transactor

class AccountService[F[_]](using F: Sync[F], L: LoggerFactory[F], T: Transactor[F]):

  given Logger[F] = LoggerFactory.getLogger

  private def getRandomBool = Random.scalaUtilRandom[F] >>= {v => v.nextBoolean}

  private val buildAccount: Boolean => F[Option[Account]] = b => if b then F.pure(Some(Account.testInstance)) else F.pure(None) 
  
  def getAccountById(id: Int): F[Option[Account]] =
    info"getAccountById($id)" >> getRandomBool >>= buildAccount

  val getAccount: Kleisli[F, Int, Option[Account]] = Kleisli(getAccountById)

  def updateAccount(account: Account): F[Account] = ??? // TODO

  def validatePassword(account: Account, password: String): F[Boolean] = ??? // TODO

  //val validatePasswordK: Kleisli[F, (Option[Account], String), Boolean] = Kleisli(validatePassword.tupled)

  // Write a function that takes an account id and a password and returns a boolean indicating whether the password is valid for the account, use composition of getAccount and validatePasswordK
  /*
  def validatePasswordForAccount(id: Int, password: String): F[Boolean] =
    getAccount.andThen(a => validatePasswordK(a, password)).run(id)
  */

end AccountService
