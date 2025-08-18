package services

import baseSpec.BaseSpecWithApplication
import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, Book}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.{ExecutionContext, Future}

class ApplicationServiceSpec extends BaseSpecWithApplication with MockFactory with ScalaFutures {

  val mockConnector = mock[LibraryConnector]
  override implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new ApplicationService(mockConnector)

  val gameOfThronesJson: JsValue = Json.obj(
    "kind" -> "books#volumes",
    "totalItems" -> 1,
    "items" -> Json.arr(
      Json.obj(
        "id" -> "someId",
        "etag" -> "etag123",
        "volumeInfo" -> Json.obj(
          "title" -> "A Game of Thrones",
          "authors" -> Json.arr("George R. R. Martin")
        )
      )
    )
  )

  "getGoogleBook" should {

    "return a book" in {
      val query = "A Game of Thrones"
      val expectedUrl = s"https://www.googleapis.com/books/v1/volumes?q=$query"

      (mockConnector.get(_: String)(_: ExecutionContext))
        .expects(expectedUrl, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThronesJson.toString()))
        .once()

      whenReady(testService.getGoogleBook(query).value) { result =>
        assert(result.isRight)
        result.foreach { book =>
          book.id shouldBe "someId"
          book.etag shouldBe "etag123"
        }
      }
    }

    "return an error if no books found" in {
      val query = "Unknown Book"
      val expectedUrl = s"https://www.googleapis.com/books/v1/volumes?q=$query"

      (mockConnector.get(_: String)(_: ExecutionContext))
        .expects(expectedUrl, *)
        .returning(EitherT.leftT[Future, String](APIError.NotFound("No books found")))
        .once()

      whenReady(testService.getGoogleBook(query).value) { result =>
        assert(result.isLeft)
        result.left.foreach { error =>
          error.httpResponseStatus shouldBe 404
        }
      }
    }
  }
}
