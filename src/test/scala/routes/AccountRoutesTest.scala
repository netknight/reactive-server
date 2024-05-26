package io.dm
package routes

import domain.Account
import routes.EntityEncoders.Implicits.Account.given

import cats.effect.{IO, Resource}
import org.http4s.Status
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.implicits.uri
import weaver.Expectations.Helpers.expect
import weaver.IOSuite

import scala.concurrent.duration.*
import scala.language.postfixOps

// TODO: Complete it


object AccountRoutesTest extends IOSuite:

  type Res = Client[IO]
  type SharedResourceType = Resource[IO, Res]

  private def buildServer() =
    App[IO].create().compile.drain

  private def buildClientResource() =
    BlazeClientBuilder[IO].resource

  private val serverFiber =
    buildServer().start

  val sharedResource: SharedResourceType =
    for {
      client <- buildClientResource()
      _ <- Resource.eval(serverFiber.andWait(5 seconds))
    } yield client
  
  /*
  // TODO: Find a way to extract Stream from resource
  private def getAccounts(client: Client[IO]): IO[fs2.Stream[IO, Account]] =
    client.get[fs2.Stream[IO, Account]](uri"http://localhost:8080/accounts")
  */

  test("Get Accounts") { client =>
    for {
      //_ <- serverFiber
      response <- client.statusFromUri(uri"http://localhost:8080/accounts")
    } yield expect.eql(response, Status.Ok)
  }




  test("Get Account by Id") { client =>
    for {
      //_ <- serverFiber
      response <- client.statusFromUri(uri"http://localhost:8080/accounts")
      //accountsResponse <- client.get[fs2.Stream[IO, Account]](uri"http://localhost:8080/accounts")
      //response <- client.statusFromUri(uri"http://localhost:8080/accounts/1")
    } yield expect.eql(response, Status.Ok)
  }

  serverFiber.flatMap(_.cancel)
