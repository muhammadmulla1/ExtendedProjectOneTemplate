package connectors

import javax.inject.Inject
import play.api.libs.json.OFormat
import play.api.libs.ws.WSClient
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {

  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): Future[Response] = {
    ws.url(url).get().map { response =>
      response.json.as[Response]
    }
  }

}