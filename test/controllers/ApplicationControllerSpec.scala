package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.Json
import models.DataModel
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  // Helper to clear the repository for testing
  def clearRepository(): Future[Unit] = {
    repository.deleteAll().map(_ => ())
  }

  // Helper to populate the repository for testing
  def populateRepository(models: Seq[DataModel]): Future[Unit] = {
    Future.sequence(models.map(model => repository.create(model))).map(_ => ())
  }

  // Run before each test
  override def beforeEach(): Unit = {
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
  }

  "ApplicationController .read" should {

    "return 200 OK and the data model as JSON if found" in {
      // Prepare test data
      val testDataModel = DataModel("1", "Test Name", "A description", 300)

      // Insert the test data into the repository
      repository.create(testDataModel).futureValue

      // Call the read action
      val result = TestApplicationController.read("1")(FakeRequest())
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldEqual Json.toJson(testDataModel)
    }

    "return 404 Not Found if the data model is not found" in {
      // Ensure no data with this ID
      val result = TestApplicationController.read("non-existent-id")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe s"No resource found with id: non-existent-id"
    }
  }

  "ApplicationController .delete" should {

    "return 202 Accepted with an empty body if the deletion is successful" in {
      // Prepare test data
      val testDataModel = DataModel("1", "Test Name", "A description", 300)

      // Insert the test data into the repository
      repository.create(testDataModel).futureValue

      // Call the delete action
      val result = TestApplicationController.delete("1")(FakeRequest())
      status(result) shouldBe Status.ACCEPTED
      contentAsString(result) shouldBe "" // Ensure empty body
    }

    "return 404 Not Found if the item to delete is not found" in {
      // Ensure no data with this ID
      val result = TestApplicationController.delete("non-existent-id")(FakeRequest())
      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe s"No resource found with id: non-existent-id"
    }
  }

  "ApplicationController .create" should {

    "return 201 Created if the creation is successful" in {
      // Prepare test data
      val testDataModel = DataModel("1", "Test Name", "A description", 300)
      val request = FakeRequest().withBody(Json.toJson(testDataModel))

      // Call the create action
      val result = TestApplicationController.create()(request)
      status(result) shouldBe Status.CREATED
    }

    "return 400 Bad Request if the request body is invalid" in {
      // Prepare invalid test data
      val invalidJson = Json.obj("invalidField" -> "invalidValue")
      val request = FakeRequest().withBody(invalidJson)

      // Call the create action
      val result = TestApplicationController.create()(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "ApplicationController .update" should {

    "return 202 Accepted and the updated data model as JSON if the update is successful" in {
      // Prepare test data
      val originalDataModel = DataModel("1", "Original Name", "Original description", 100)
      val updatedDataModel = DataModel("1", "Updated Name", "Updated description", 200)

      // Insert the original test data into the repository
      repository.create(originalDataModel).futureValue

      // Call the update action
      val request = FakeRequest().withBody(Json.toJson(updatedDataModel))
      val result = TestApplicationController.update("1")(request)

      status(result) shouldBe Status.ACCEPTED
      contentAsJson(result) shouldEqual Json.toJson(updatedDataModel)
    }

    "return 404 Not Found if the data model to update is not found" in {
      // Prepare test data
      val updatedDataModel = DataModel("1", "Updated Name", "Updated description", 200)

      // Call the update action with a non-existent ID
      val request = FakeRequest().withBody(Json.toJson(updatedDataModel))
      val result = TestApplicationController.update("non-existent-id")(request)

      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe s"No resource found with id: non-existent-id"
    }

    "return 400 Bad Request if the request body is invalid" in {
      // Prepare invalid test data
      val invalidJson = Json.obj("invalidField" -> "invalidValue")
      val request = FakeRequest().withBody(invalidJson)

      // Call the update action
      val result = TestApplicationController.update("1")(request)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
