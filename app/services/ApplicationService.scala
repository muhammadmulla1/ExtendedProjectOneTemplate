package services

import connectors.LibraryConnector
import javax.inject.{Inject, Singleton}
import models.{GoogleBooksResponse}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[GoogleBooksResponse] = {
    val url = urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search+$term")
    connector.get[GoogleBooksResponse](url)
  }
}
