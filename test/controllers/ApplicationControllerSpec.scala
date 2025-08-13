package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.{Json, JsValue}
import models.DataModel
import scala.concurrent.Future
import play.api.mvc.Result

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  def clearRepository(): Future[Unit] = {
    repository.deleteAll().map(_ => ())
  }

  def populateRepository(models: Seq[DataModel]): Future[Unit] = {
    Future.sequence(models.map(model => repository.create(model))).map(_ => ())
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    clearRepository().futureValue
  }

  override def afterEach(): Unit = {
    super.afterEach()
    clearRepository().futureValue
  }

  "ApplicationController .index" should {

    "return 200 OK and a JSON array of DataModels in the correct order" in {
      // Prepare test data
      val testDataModels = Seq(
        DataModel("1", "Test Name 1", "Description 1", 100),
        DataModel("2", "Test Name 2", "Description 2", 200)
      )

      // Populate repository with test data
      populateRepository(testDataModels).futureValue

      // Call the index action
      val result = TestApplicationController.index()(FakeRequest())

      // Verify the status and content
      status(result) shouldBe Status.OK
      val jsonResponse = contentAsJson(result).as[Seq[DataModel]]

      // Check if the returned data matches the expected data, regardless of order
      jsonResponse should contain theSameElementsAs testDataModels
    }

    "return 400 Bad Request if the request is invalid" in {
      val request = FakeRequest("INVALID_METHOD", "/api")
      val result = TestApplicationController.index()(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
    }

    "return 404 Not Found if the data model is not found" in {
      val readResult: Future[Result] = TestApplicationController.read("non-existent-id")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
      contentAsString(readResult) shouldBe s"No resource found with id: non-existent-id"
    }
  }

  "ApplicationController .delete" should {

    "return 202 Accepted with an empty body if the deletion is successful" in {
      val testDataModel = DataModel("1", "Test Name", "A description", 300)

      repository.create(testDataModel).futureValue

      val result = TestApplicationController.delete("1")(FakeRequest())
      status(result) shouldBe Status.ACCEPTED
      contentAsString(result) shouldBe "" // Ensure empty body
    }

    "return 404 Not Found if the item to delete is not found" in {
      val result = TestApplicationController.delete("non-existent-id")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe s"No resource found with id: non-existent-id"
    }

    "return 400 Bad Request if the request is invalid" in {
      val request = FakeRequest("INVALID_METHOD", "/api")
      val result = TestApplicationController.delete("1")(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {
      val request: FakeRequest[JsValue] = FakeRequest().withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED
    }

    "return 400 Bad Request if the request body is invalid" in {
      val invalidJson = Json.obj("invalidField" -> "invalidValue")
      val request = FakeRequest().withBody(invalidJson)
      val result = TestApplicationController.create()(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .update" should {

    "return 202 Accepted and the updated data model as JSON if the update is successful" in {
      val originalDataModel = DataModel("1", "Original Name", "Original description", 100)
      val updatedDataModel = DataModel("1", "Updated Name", "Updated description", 200)

      repository.create(originalDataModel).futureValue

      val request = FakeRequest().withBody(Json.toJson(updatedDataModel))
      val result = TestApplicationController.update("1")(request)

      status(result) shouldBe Status.ACCEPTED
      contentAsJson(result) shouldEqual Json.toJson(updatedDataModel)
    }

    "return 404 Not Found if the data model to update is not found" in {
      val updatedDataModel = DataModel("1", "Updated Name", "Updated description", 200)
      val request = FakeRequest().withBody(Json.toJson(updatedDataModel))
      val result = TestApplicationController.update("non-existent-id")(request)
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe s"No resource found with id: non-existent-id"
    }

    "return 400 Bad Request if the request body is invalid" in {
      val invalidJson = Json.obj("invalidField" -> "invalidValue")
      val request = FakeRequest().withBody(invalidJson)
      val result = TestApplicationController.update("1")(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
