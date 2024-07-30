package controllers

import play.api.mvc.{BaseController, ControllerComponents, Action, AnyContent}

import javax.inject.{Inject, Singleton}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index() = TODO

  def create() = TODO

  def read(id: String): Action[AnyContent] = Action { implicit request =>
    Ok(s"Reading resource with id: $id")
  }

  def update(id: String) = TODO

  def delete(id: String) = TODO
}
