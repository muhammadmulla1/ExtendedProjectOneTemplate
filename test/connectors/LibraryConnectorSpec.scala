package connectors

import baseSpec.BaseSpecWithApplication
import models.APIError
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT

class LibraryConnectorSpec extends BaseSpecWithApplication with MockFactory with ScalaFutures {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  "LibraryConnector" should {

    "return Right[String] when API responds with 200" in {
      val mockWSClient = mock[WSClient]
      val mockRequest = mock[WSRequest]
      val mockResponse = mock[WSResponse]

      val testUrl = "https://www.googleapis.com/books/v1/volumes?q=test"
      val connector = new LibraryConnector(mockWSClient)

      (mockWSClient.url _).expects(testUrl).returning(mockRequest)
      (mockRequest.get _).expects().returning(Future.successful(mockResponse))
      (() => mockResponse.status).expects().returning(200)
      (() => mockResponse.body).expects().returning("""{"foo":"bar"}""")

      val result: EitherT[Future, APIError, String] = connector.get(testUrl)
      whenReady(result.value) { res =>
        res.isRight shouldBe true
        res.right.foreach(_ should include("foo"))
      }
    }

    "return Left[APIError] when API responds with non-200 status" in {
      val mockWSClient = mock[WSClient]
      val mockRequest = mock[WSRequest]
      val mockResponse = mock[WSResponse]

      val testUrl = "https://www.googleapis.com/books/v1/volumes?q=test"
      val connector = new LibraryConnector(mockWSClient)

      (mockWSClient.url _).expects(testUrl).returning(mockRequest)
      (mockRequest.get _).expects().returning(Future.successful(mockResponse))
      (() => mockResponse.status).expects().returning(404)
      (() => mockResponse.statusText).expects().returning("Not Found")

      val result: EitherT[Future, APIError, String] = connector.get(testUrl)
      whenReady(result.value) { res =>
        res.isLeft shouldBe true
        res.left.foreach(err => err.httpResponseStatus shouldBe 404)
      }
    }

    "return Left[APIError] if request fails with exception" in {
      val mockWSClient = mock[WSClient]
      val mockRequest = mock[WSRequest]

      val testUrl = "https://www.googleapis.com/books/v1/volumes?q=test"
      val connector = new LibraryConnector(mockWSClient)

      (mockWSClient.url _).expects(testUrl).returning(mockRequest)
      (mockRequest.get _).expects().returning(Future.failed(new RuntimeException("Connection error")))

      val result: EitherT[Future, APIError, String] = connector.get(testUrl)
      whenReady(result.value) { res =>
        res.isLeft shouldBe true
        res.left.foreach(err => err.reason should include("Connection error"))
      }
    }
  }
}
