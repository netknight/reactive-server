package io.dm
package routes

import domain.FileMetadataMutation
import routes.EntityEncoders.Implicits.given
import routes.FileRoutes.RoutePath
import service.FileService

import cats.effect.Concurrent
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import org.http4s.HttpRoutes
import org.typelevel.log4cats.syntax.LoggerInterpolator
import org.typelevel.log4cats.{Logger, LoggerFactory}

class FileRoutes[F[_]](using F: Concurrent[F], H: HttpRoutesErrorHandler[F, _], L: LoggerFactory[F], val fileService: FileService[F]) extends Route[F] {
  given Logger[F] = LoggerFactory.getLogger

  override val path: RoutePath.type = RoutePath

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root =>
      info"GET ${path.base}" >> Ok(fileService.findAll())

    case GET -> Root / FileIdVar(id) =>
      for {
        _ <- info"GET ${path.base}/$id"
        file <- fileService.findById(id)
        response <- okOrNotFound(file)
      } yield response

    // TODO: Remove it (this implementation only required until upload is implemented)
    case req @ POST -> Root =>
      for {
        _ <- info"POST ${path.base}"
        file <- req.as[FileMetadataMutation]
        result <- fileService.create(file)
        response <- Created(fileService.create(file))
      } yield response

    case req @ PUT -> Root / FileIdVar(id) =>
      for {
        _ <- info"PUT ${path.base}/$id"
        file <- req.as[FileMetadataMutation]
        response <- fileService.update(id, file) >>= noContentOrNotFound
      } yield response

    case req @ DELETE -> Root / FileIdVar(id) =>
      for {
        _ <- info"DELETE ${path.base}/$id"
        response <- fileService.delete(id) >>= noContentOrNotFound
      } yield response


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
