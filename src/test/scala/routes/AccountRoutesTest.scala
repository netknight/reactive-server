package io.dm
package routes

import repositories.{AccountEntity, AccountFields, AccountRepository}
import service.AccountService

import cats.effect.{Sync, IO}
import cats.syntax.applicative.*
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{Logger, LoggerFactory}
import weaver.Expectations.Helpers.expect
import weaver.SimpleIOSuite

class TestAccountRepository[F[_]: Sync](data: Seq[AccountEntity]) extends AccountRepository.I[F] {

  override def get(id: Long): F[Option[AccountEntity]] = data.find(_.id == id).pure[F]

  override def delete(id: Long): F[Boolean] = data.exists(_.id == id).pure[F]

  override def create(a: AccountFields): F[AccountEntity] =
    AccountEntity(
      id = data.maxBy(_.id).id + 1,
      username = a.username,
      email = a.email,
      password = a.password,
    ).pure[F]

  override def update(id: Long, a: AccountFields): F[AccountEntity] = data.find(_.id == id).map(_.copy(
    username = a.username,
    email = a.email,
    password = a.password,
  )).getOrElse(throw new IllegalArgumentException("No such ID!")).pure[F]

  override def list(): fs2.Stream[F, AccountEntity] = fs2.Stream.fromIterator(data.iterator, 1)
}

object AccountRoutesTest extends SimpleIOSuite {

  given AccountRepository.I[IO] = TestAccountRepository(Seq(
    AccountEntity(1, "test1", "test@test.com", "test1234")
  ))

  given LoggerFactory[IO] = Slf4jFactory.create
  given Logger[IO] = LoggerFactory.getLogger


  test("AccountRouteTest") {
    for {
      r <- AccountService[IO]().findById(1)
      //r  <- s.findById(1)
    } yield expect.eql(r.map(_.username), Some("test1"))
  }

}
