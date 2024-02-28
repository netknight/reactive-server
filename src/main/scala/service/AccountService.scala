package io.dm
package service

import domain.Account
import domain.Account.mapToAccount
import repositories.{AccountFields, AccountRepository, IdObject, OpResult, OpResultAffectedRows}

import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.functor.*
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import fs2.Stream
import org.typelevel.log4cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

class AccountService[F[_]](using F: Sync[F], L: LoggerFactory[F]/*, T: Transactor[F]*/, R: AccountRepository.I[F]):

  given Logger[F] = LoggerFactory.getLogger


  def findById(id: Long): F[OpResult[Account]] =
    info"getAccountById($id)" >> R.get(id).mapToAccount()
      //.mapToAccount()

  //def findAll(): Stream[F, Account] =
    //info"findAll()" >> R.list().flatMap(v => fromEntityF[F](v))
  def findAll(): F[List[Account]] = ???
    //info"getAccounts()" >> R.list().mapToAccount() // TODO: Uncomment this

  //val getAccount: Kleisli[F, Int, Option[Account]] = Kleisli(findById)

  def createAccount(account: Account): F[IdObject[Long]] = R.create(AccountFields(account.username, account.email, account.password))

  def updateAccount(id: Long, account: Account): F[OpResultAffectedRows] = R.update(id, AccountFields(account.username, account.email, account.password))

  def deleteAccount(id: Long): F[OpResultAffectedRows] = R.delete(id)

  def validatePassword(account: Account, password: String): F[Boolean] = ??? // TODO

  //val validatePasswordK: Kleisli[F, (Option[Account], String), Boolean] = Kleisli(validatePassword.tupled)

  // Write a function that takes an account id and a password and returns a boolean indicating whether the password is valid for the account, use composition of getAccount and validatePasswordK
  /*
  def validatePasswordForAccount(id: Int, password: String): F[Boolean] =
    getAccount.andThen(a => validatePasswordK(a, password)).run(id)
  */

end AccountService
