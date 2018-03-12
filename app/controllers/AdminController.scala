package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class AdminController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action{ implicit request =>
    Ok(views.html.admin)
  }
}
