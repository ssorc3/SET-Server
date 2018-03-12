package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.http.HttpErrorHandler
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class AdminController @Inject() (assets: Assets, errorHandler: HttpErrorHandler, config: Configuration, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc)
{
  def index: Action[AnyContent] = Action(Ok)
}
