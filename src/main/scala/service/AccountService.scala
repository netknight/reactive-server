package io.dm
package service

import domain.{Account, AccountId, AccountMutation}
import repositories.{AccountRepository, IdObject, OpResult, OpResultAffectedRows}

import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.flatMap.*
import fs2.Stream
import org.typelevel.log4cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

class AccountService[F[_]](using F: Sync[F], L: LoggerFactory[F]/*, T: Transactor[F]*/, R: AccountRepository.I[F]):

  given Logger[F] = LoggerFactory.getLogger


  def findById(id: AccountId): F[OpResult[Account]] =
    info"getAccountById($id)" >> R.get(id)


  def findAll(): Stream[F, Account] =
    Stream.eval(info"getAccounts()") >> R.list()

  val getAccount: Kleisli[F, AccountId, OpResult[Account]] = Kleisli(findById)

  def createAccount(account: AccountMutation): F[IdObject[AccountId]] =
    R.create(account)

  def updateAccount(id: AccountId, account: AccountMutation): F[OpResultAffectedRows] =
    R.update(id, account)

  def deleteAccount(id: AccountId): F[OpResultAffectedRows] = R.delete(id)

  def validatePassword(account: Account, password: String): F[Boolean] = ??? // TODO

  //val validatePasswordK: Kleisli[F, (Option[Account], String), Boolean] = Kleisli(validatePassword.tupled)

  // Write a function that takes an account id and a password and returns a boolean indicating whether the password is valid for the account, use composition of getAccount and validatePasswordK
  /*
  def validatePasswordForAccount(id: Int, password: String): F[Boolean] =
    getAccount.andThen(a => validatePasswordK(a, password)).run(id)
  */

end AccountService
