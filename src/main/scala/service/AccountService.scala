package io.dm
package service

import domain.{Account, AccountId, AccountMutation, IdObject}
import repositories.{AccountRepository, OpResult, OpResultEmpty}

import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.functor.*
import cats.syntax.flatMap.*
import fs2.Stream
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

import java.time.Instant

class AccountService[F[_]](using F: Sync[F], L: LoggerFactory[F]/*, T: Transactor[F]*/, R: AccountRepository.I[F]):

  given Logger[F] = LoggerFactory.getLogger

  def findAll(): Stream[F, Account] =
    Stream.eval(info"getAccounts()") >> R.list()

  def findById(id: AccountId): F[OpResult[Account]] =
    info"getAccountById($id)" >> R.get(id)

  val getAccount: Kleisli[F, AccountId, OpResult[Account]] = Kleisli(findById)

  def create(account: AccountMutation): F[IdObject[AccountId]] =
    R.create(Account(AccountId.generate, Instant.now, Instant.now, account))
      .map(entity => IdObject(entity.id))

  def update(id: AccountId, account: AccountMutation): F[OpResultEmpty] =
    R.get(id).map(opResult =>
      opResult
        .map(entity => entity.copy(body = account, updated = Instant.now))
        .map(entity => R.update(entity) map OpResult.mapToEmpty)
    )

  def delete(id: AccountId): F[OpResultEmpty] = R.delete(id) map OpResult.mapToEmpty

  def validatePassword(account: Account, password: String): F[Boolean] = ??? // TODO

  //val validatePasswordK: Kleisli[F, (Option[Account], String), Boolean] = Kleisli(validatePassword.tupled)

  // Write a function that takes an account id and a password and returns a boolean indicating whether the password is valid for the account, use composition of getAccount and validatePasswordK
  /*
  def validatePasswordForAccount(id: Int, password: String): F[Boolean] =
    getAccount.andThen(a => validatePasswordK(a, password)).run(id)
  */

end AccountService
