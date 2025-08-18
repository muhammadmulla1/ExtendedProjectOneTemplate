package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.{JsValue, Json}
import models.{APIError, DataModel}
import scala.concurrent.Future
import play.api.mvc.Result
import repositories.DataRepository
import services.ApplicationService
import org.scalatest.matchers.must.Matchers._
import org.mockito.MockitoSugar.mock
import org.mockito.Mockito._

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val mockDataRepository: DataRepository = mock[DataRepository]
  val mockApplicationService: ApplicationService = mock[ApplicationService]

  val TestApplicationController = new ApplicationController(
    component,
    mockDataRepository,
    mockApplicationService
  )

  "ApplicationController" should {

    "return a list of books" in {
      val testData = Seq(DataModel("1", "Book One", "Author One", 123))
      when(mockDataRepository.index())
        .thenReturn(Future.successful(Right(testData)))

      val result: Future[Result] =
        TestApplicationController.index().apply(FakeRequest(GET, "/api"))

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(testData)
    }

    "find a book by id" in {
      val testBook = DataModel("1", "Book One", "Author One", 123)
      when(mockDataRepository.read("1"))
        .thenReturn(Future.successful(Right(testBook)))

      val result: Future[Result] =
        TestApplicationController.read("1").apply(FakeRequest(GET, "/api/1"))

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(testBook)
    }

    "return 404 if book not found" in {
      when(mockDataRepository.read("999"))
        .thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Not found"))))

      val result: Future[Result] =
        TestApplicationController.read("999").apply(FakeRequest(GET, "/api/999"))

      status(result) mustBe Status.NOT_FOUND
    }

    "create a new book" in {
      val newBook = DataModel("2", "Book Two", "Author Two", 200)
      val jsonBody: JsValue = Json.toJson(newBook)

      when(mockDataRepository.create(newBook))
        .thenReturn(Future.successful(newBook))

      val result: Future[Result] =
        TestApplicationController.create().apply(FakeRequest(POST, "/api/create").withBody(jsonBody))

      status(result) mustBe Status.CREATED
      contentAsJson(result) mustBe Json.toJson(newBook)
    }

    "update an existing book" in {
      val updatedBook = DataModel("1", "Updated Book", "Updated Author", 150)
      val jsonBody: JsValue = Json.toJson(updatedBook)

      when(mockDataRepository.update("1", updatedBook))
        .thenReturn(Future.successful(Right(updatedBook)))

      val result: Future[Result] =
        TestApplicationController.update("1").apply(FakeRequest(PUT, "/api/1").withBody(jsonBody))

      status(result) mustBe Status.OK
      contentAsJson(result) mustBe Json.toJson(updatedBook)
    }

    "return 404 if updating a non-existent book" in {
      val updatedBookNotFound = DataModel("999", "Non-existent", "Nobody", 0)
      val jsonBody: JsValue = Json.toJson(updatedBookNotFound)

      when(mockDataRepository.update("999", updatedBookNotFound))
        .thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Not found"))))

      val result: Future[Result] =
        TestApplicationController.update("999").apply(FakeRequest(PUT, "/api/999").withBody(jsonBody))

      status(result) mustBe Status.NOT_FOUND
    }

    "delete a book" in {
      val deletedBook = DataModel("1", "Book One", "Author One", 123)
      when(mockDataRepository.delete("1"))
        .thenReturn(Future.successful(Right(deletedBook)))

      val result: Future[Result] =
        TestApplicationController.delete("1").apply(FakeRequest(DELETE, "/api/1"))

      status(result) mustBe Status.NO_CONTENT
    }

    "return 404 if deleting a non-existent book" in {
      when(mockDataRepository.delete("999"))
        .thenReturn(Future.successful(Left(APIError.BadAPIResponse(404, "Not found"))))

      val result: Future[Result] =
        TestApplicationController.delete("999").apply(FakeRequest(DELETE, "/api/999"))

      status(result) mustBe Status.NOT_FOUND
    }
  }
}
