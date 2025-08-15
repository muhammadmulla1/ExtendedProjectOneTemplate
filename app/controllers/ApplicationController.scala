package controllers

import models.{APIError, DataModel}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import services.ApplicationService
import repositories.DataRepository

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository,
                                       val service: ApplicationService)
                                       (implicit val ec: ExecutionContext)
                                      extends BaseController {


  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(items) => Ok(Json.toJson(items))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map { createdBook =>
          Created(Json.toJson(createdBook))
        }.recover {
          case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
    }
  }

  def read(id: String) = Action.async {
    dataRepository.read(id).map {
      case Right(book) => Ok(Json.toJson(book))
      case Left(APIError.BadAPIResponse(status, message)) => Status(status)(message)
    }
  }




  def update(id: String) = Action.async(parse.json) { request =>
    val book = request.body.as[DataModel]
    dataRepository.update(id, book).map {
      case Right(updated) => Ok(Json.toJson(updated))
      case Left(err) => Status(err.upstreamStatus)(err.upstreamMessage)
    }
  }


  def delete(id: String) = Action.async { implicit request =>
    dataRepository.delete(id).map {
      case Right(_) => NoContent
      case Left(err) => Status(err.upstreamStatus)(err.upstreamMessage)
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok(Json.toJson(book))
      case Left(error) => Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }.recover {
      case ex => InternalServerError(Json.obj("status" -> "error", "message" -> ex.getMessage))
    }
  }
}
