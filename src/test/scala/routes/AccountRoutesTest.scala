package io.dm
package routes

import domain.{Account, AccountId, AccountMutation, IdObject}
import repositories.*
import repositories.OpResult.{toOpResult, toOpResultAffectedRows}
import server.HttpServer
import service.AccountService

import cats.Eval
import cats.effect.{IO, Sync}
import cats.syntax.applicative.*
import io.circe.Json
import io.github.iltotore.iron.autoRefine
import org.http4s.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.implicits.uri
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.{Logger, LoggerFactory}
import weaver.Expectations.Helpers.expect
import weaver.SimpleIOSuite

import scala.concurrent.ExecutionContext

// TODO: Complete it

import java.time.Instant

/*
class TestAccountRepository[F[_]: Sync](data: Seq[Account]) extends AccountRepository.I[F] {
  override def get(id: AccountId): F[OpResultEntity[Account]] =
    data.find(_.id == id).toOpResult.pure[F]

  override def delete(id: AccountId): F[OpResultAffectedRows] =
    Eval.now(data.exists(_.id == id)).toOpResultAffectedRows.pure[F]

  override def create(entity: Account): F[Account] =
    IdObject(AccountId.applyUnsafe(scala.util.Random.nextLong())).pure[F]
    /*
    Account(
      id = data.maxBy(_.id).id + 1,
      username = entity.username,
      email = entity.email,
      password = entity.password,
    ).pure[F]
    */

  override def update(id: AccountId, mutation: AccountMutation): F[OpResultAffectedRows] =
    data.find(_.id == id).map(_.copy(
      body = mutation,
    )).toOpResult.map(_ => 1).pure[F]

  override def list(): fs2.Stream[F, Account] = fs2.Stream.fromIterator(data.iterator, 1)
}

object AccountRoutesTest extends SimpleIOSuite {

  given AccountRepository.I[IO] = TestAccountRepository(Seq(
    Account(AccountId(1), Instant.now, Instant.now, AccountMutation("test1", "test@test.com", "test1234")),
  ))

  given LoggerFactory[IO] = Slf4jFactory.create
  given Logger[IO] = LoggerFactory.getLogger


  test("AccountRouteTest") {
    for {
      r <- AccountService[IO]().findById(1)
      //r  <- s.findById(1)
    } yield expect.eql(r.toOption.map(_.body.username), Some("test1"))
  }

}
*/

/*
object AccountRoutesTest extends SimpleIOSuite:
  import cats.effect.unsafe.implicits.global

  test("AccountRouteTest") {
    val test = for {
      server <- App[IO].create()
      client <- fs2.Stream.resource(BlazeClientBuilder[IO].resource)
      result <- fs2.Stream.eval(
        for {
          response <- client.statusFromUri(uri"http://localhost:8080/accounts")
          _ <- IO(println(response))
        } yield response
      )
    } yield expect.eql(result, Status.Ok)
    test.compile.lastOrError
  }
*/
