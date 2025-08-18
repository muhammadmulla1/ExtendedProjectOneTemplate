package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.ws.WSClient
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {

  def get(url: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, String] = {
    val responseFuture: Future[Either[APIError, String]] = ws.url(url).get().map { response =>
      response.status match {
        case 200 => Right(response.body)
        case other =>
          Left(APIError.BadAPIResponse(other, response.statusText))
      }
    }.recover { case ex: Throwable =>
      Left(APIError.BadAPIResponse(0, ex.getMessage))
    }

    EitherT(responseFuture)
  }
}
