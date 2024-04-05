package io.dm
package routes

//import domain.FileMetadata
import routes.AccountRoutes.RoutePath
import service.FileService

import cats.effect.Concurrent
import cats.syntax.flatMap.*
import org.http4s.HttpRoutes
import org.typelevel.log4cats.LoggerFactory

class FileRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val fileService: FileService[F]) extends Route[F] {

  override val path: RoutePath.type = RoutePath

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok("Hello, World!")
          /*
    case GET -> Root / "files" / "list" =>
      fileService.findAll().flatMap(Ok(_))
           */
      /*
    case GET -> Root / "files" / "download" / fileName =>
      fileService.downloadFile(fileName).flatMap {
        case Some(file) => Ok(file)
        case None => NotFound()
      }

    case req @ POST -> Root / "files" / "upload" =>
      req.decode[Multipart[F]] { multipart =>
        fileService.uploadFile(multipart).flatMap(Created(_))
      }
       */
  }

}

object FileRoutes {
  object RoutePath extends RoutePathObject {
    override def base: String = "/files"
  }
}
