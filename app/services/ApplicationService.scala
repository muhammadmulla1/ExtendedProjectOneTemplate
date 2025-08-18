package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, Book}
import play.api.libs.json.Json
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationService @Inject()(connector: LibraryConnector)(implicit ec: ExecutionContext) {

  def getGoogleBook(query: String): EitherT[Future, APIError, Book] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=$query"

    connector.get(url).subflatMap { jsonString =>
      val json = Json.parse(jsonString)
      (json \ "items").asOpt[Seq[play.api.libs.json.JsValue]] match {
        case Some(items) if items.nonEmpty =>
          val bookJson = items.head
          val id = (bookJson \ "id").as[String]
          val etag = (bookJson \ "etag").as[String]

          val volumeInfo = bookJson \ "volumeInfo"
          val title = (volumeInfo \ "title").asOpt[String].getOrElse("Unknown Title")
          val description = (volumeInfo \ "description").asOpt[String].getOrElse("No description")
          val pageCount = (volumeInfo \ "pageCount").asOpt[Int].getOrElse(0)

          Right(Book(id, etag, title, description, pageCount))
        case _ =>
          Left(APIError.BadAPIResponse(404, "No books found"))
      }
    }
  }
}
