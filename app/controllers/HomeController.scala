package controllers

import javax.inject.Inject

import models.{DeviceRepository, SensorDataRepository, UserRepository}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class HomeController @Inject()(cc: ControllerComponents, users: UserRepository, devices: DeviceRepository, sensors: SensorDataRepository)(implicit ec: ExecutionContext) extends AbstractController(cc){
  def index() = Action { implicit request =>
    Ok("Hello, World!")
  }
  
  def delete = Action { implicit request =>
    users.delete()
    devices.delete()
    sensors.delete()
    Ok("Deleted")
  }
}
