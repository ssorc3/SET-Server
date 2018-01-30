package controllers

import javax.inject.Inject

import models.{DeviceRepository, SensorDataRepository, UserRepository}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc){
  def index() = Action { implicit request =>
    Ok(request.body)
  }
}
