package io.dm
package service

import domain.Account
import domain.Account.mapToAccount
import repositories.{AccountEntity, AccountRepository}

import cats.data.Kleisli
import cats.effect.Sync
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import org.typelevel.log4cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}

class AccountService[F[_]](using F: Sync[F], L: LoggerFactory[F]/*, T: Transactor[F]*/, R: AccountRepository.I[F]):

  given Logger[F] = LoggerFactory.getLogger

  //private def getRandomBool = Random.scalaUtilRandom[F] >>= {v => v.nextBoolean}

  //private val buildAccount: Boolean => F[Option[Account]] = b => if b then F.pure(Some(Account.testInstance)) else F.pure(None)
  
  /*
  // TODO: This can be common function for all services that is build 
  extension [G[_]: Functor](v: F[G[AccountEntity]])
    def mapToAccount(): F[G[Account]] = v.nested.map(Account.fromEntity).value    
  */

  def findById(id: Int): F[Option[Account]] =
    //info"getAccountById($id)" >> getRandomBool >>= buildAccount
    info"getAccountById($id)" >> R.get(id).mapToAccount()  // TODO: Uncomment this when deal with DB hanging issue
    //info"getAccountById($id)" >> Option(AccountEntity(1, "test3", "test@test.com", "iddQd43")).pure[F].mapToAccount()


  def findAll(): F[List[Account]] = ???
    //info"getAccounts()" >> R.list().mapToAccount() // TODO: Uncomment this
  
  val getAccount: Kleisli[F, Int, Option[Account]] = Kleisli(findById)

  def updateAccount(account: Account): F[Account] = ??? // TODO

  def validatePassword(account: Account, password: String): F[Boolean] = ??? // TODO

  //val validatePasswordK: Kleisli[F, (Option[Account], String), Boolean] = Kleisli(validatePassword.tupled)

  // Write a function that takes an account id and a password and returns a boolean indicating whether the password is valid for the account, use composition of getAccount and validatePasswordK
  /*
  def validatePasswordForAccount(id: Int, password: String): F[Boolean] =
    getAccount.andThen(a => validatePasswordK(a, password)).run(id)
  */

end AccountService
