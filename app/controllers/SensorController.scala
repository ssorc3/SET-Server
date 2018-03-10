package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import com.google.inject.Singleton
import models.SensorData
import play.api.libs.json.JsValue
import play.api.mvc._
import repositories.{DeviceRepository, ScriptRepository, SensorDataRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SensorController @Inject()(cc: MessagesControllerComponents, auth: SecuredAuthenticator, devices: DeviceRepository, sensors: SensorDataRepository, scripts: ScriptRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  def registerDevice(deviceID: String): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val userID = request.user.userID
    val deviceName = (request.body \ "deviceName").as[String]
    devices.exists(deviceID).flatMap{
      case false =>
        devices.create(deviceID, userID, deviceName).map(_ => Ok("Device Registered"))
      case true => Future.successful(BadRequest("Device already registered"))
    }
  }

  def rename(deviceID: String): Action[JsValue] = auth.JWTAuthentication.async(parse.json) {implicit request =>
    val userID = request.user.userID
    val deviceName = (request.body \ "deviceName").as[String]
    devices.exists(deviceID).flatMap{
      case true =>
        devices.rename(deviceID, userID, deviceName).map(_ => Ok("Device renamed"))
      case false => Future.successful(BadRequest("Device has not yet been registered"))
    }
  }

  def getUserDevices: Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) { implicit request =>
    val userID = request.user.userID
    devices.getUserDevices(userID).map(d => Ok(d))
  }

  def deleteUserDevice(deviceID: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) { implicit request =>
    val userID = request.user.userID
    devices.delete(deviceID, userID).map{
      case true =>
        Ok("Device has been deleted")
      case false =>
        BadRequest("Either the device doesn't exist or the user provided does not own the device")
    }
  }

  // TODO: def signalUserDevice(deviceID: String): Action[AnyContent] = auth.JWTAuthentication.async

  def receiveData(sensorType: Int, deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) {implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Double = (request.body \ "value").as[Double]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        val result: Option[Future[Int]] = sensorType match {
          case 0 => Some(sensors.addTemperatureReading(value, deviceID, timestamp))
          case 1 => Some(sensors.addHumidityReading(value, deviceID, timestamp))
          case 2 => Some(sensors.addLightReading(value, deviceID, timestamp))
          case 3 => Some(sensors.addNoiseReading(value, deviceID, timestamp))
          case _ => None
        }
        result.fold(Future.successful(BadRequest("Invalid sensor type")))(_.map(v => Ok(v)))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def getData(sensorType: Int, deviceID: String, page: Int): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) {implicit request =>
    devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap{
      case true =>
        val result: Option[Future[Seq[SensorData]]] = sensorType match {
          case 0 => Some(sensors.getTemperatures(deviceID, page))
          case 1 => Some(sensors.getHumidity(deviceID, page))
          case 2 => Some(sensors.getLight(deviceID, page))
          case 3 => Some(sensors.getNoise(deviceID, page))
          case _ => None
        }
        result.fold(Future.successful(BadRequest("Invalid sensor type")))(_.map(v => Ok(v)))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def getTimeTemperature(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) {implicit request =>
     devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap {
       case true =>
         time match {
           case "10mins" => sensors.getMinuteTemperatures(deviceID).map(t => Ok(t))
           case "hour" => sensors.getHourTemperatures(deviceID).map(t => Ok(t))
           case "day" => sensors.getDayTemperatures(deviceID).map(t => Ok(t))
           case _ => Future.successful(NotFound)
         }
       case false => Future.successful(BadRequest("Invalid device"))
     }
  }

  def getTimeHumidity(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) {implicit request =>
     devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap {
       case true =>
         time match {
           case "10mins" => sensors.getMinuteTemperatures(deviceID).map(t => Ok(t))
           case "hour" => sensors.getHourTemperatures(deviceID).map(t => Ok(t))
           case "day" => sensors.getDayTemperatures(deviceID).map(t => Ok(t))
           case _ => Future.successful(NotFound)
         }
       case false => Future.successful(BadRequest("Invalid device"))
     }
  }

  def getTimeLight(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) {implicit request =>
     devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap {
       case true =>
         time match {
           case "10mins" => sensors.getMinuteLights(deviceID).map(t => Ok(t))
           case "hour" => sensors.getHourLights(deviceID).map(t => Ok(t))
           case "day" => sensors.getDayLights(deviceID).map(t => Ok(t))
           case _ => Future.successful(NotFound)
         }
       case false => Future.successful(BadRequest("Invalid device"))
     }
  }

  def getTimeNoise(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) {implicit request =>
     devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap {
       case true =>
         time match {
           case "10mins" => sensors.getMinuteNoise(deviceID).map(t => Ok(t))
           case "hour" => sensors.getHourNoise(deviceID).map(t => Ok(t))
           case "day" => sensors.getDayNoise(deviceID).map(t => Ok(t))
           case _ => Future.successful(NotFound)
         }
       case false => Future.successful(BadRequest("Invalid device"))
     }
  }
}
