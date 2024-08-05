package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import repositories.DataRepository

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository,
                                       implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(items: Seq[DataModel]) => Ok(Json.toJson(items))
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case Some(dataModel) => Ok(Json.toJson(dataModel))
      case None => NotFound(s"No resource found with id: $id")
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.delete(id).map { deleteResult =>
      if (deleteResult.getDeletedCount > 0) {
        Accepted // No body
      } else {
        NotFound(s"No resource found with id: $id")
      }
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).map {
          case Some(updatedDataModel) => Accepted(Json.toJson(updatedDataModel))
          case None => NotFound(s"No resource found with id: $id")
        }
      case JsError(_) => Future.successful(BadRequest)
    }
  }
}
