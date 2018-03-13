package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import com.google.inject.Singleton
import models.SensorData
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc._
import repositories._
import services.{ActuatorService, ScriptService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SensorController @Inject()(cc: MessagesControllerComponents, auth: SecuredAuthenticator,
                                 devices: DeviceRepository, sensors: SensorDataRepository,
                                 scripts: ScriptRepository, ws: WSClient,
                                 config: Configuration, scriptRunner: ScriptService,
                                 users: UserRepository, actuators: ActuatorService,
                                 zones: ZoneRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

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

  def signalUserDevice(deviceID: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) { implicit request =>
    val req: WSRequest = ws.url("https://api.particle.io/v1/devices/" + deviceID + "/flash")
    val result = req.post(Map("args" -> "", "access_token" -> config.get[String]("particle_access_token")))
    result.map(r => Ok(Json.obj("result" -> r.status)))
  }

  def receiveData(sensorType: Int, deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) {implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Double = (request.body \ "value").as[Double]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        val result: Option[Future[Int]] = sensorType match {
          case 0 =>
            devices.getOwnerID(deviceID).map { us =>
              val userID = us.headOption.getOrElse("")
              compareTemp(userID, value)
            }
            Some(sensors.addTemperatureReading(value, deviceID, timestamp))
          case 1 => Some(sensors.addHumidityReading(value, deviceID, timestamp))
          case 2 => Some(sensors.addLightReading(value, deviceID, timestamp))
          case 3 => Some(sensors.addNoiseReading(value, deviceID, timestamp))
          case _ => None
        }
        result.fold(Future.successful(BadRequest("Invalid sensor type")))(_.map(_ => Ok))
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

  def getTimeTemperature(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) {implicit request =>
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

  def getTimeHumidity(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) {implicit request =>
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

  def getTimeLight(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) {implicit request =>
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

  def getTimeNoise(deviceID: String, time: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) {implicit request =>
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

  def motionDetected(deviceID: String): Action[AnyContent] = Action.async(parse.default) { implicit request =>
    println("Motion detected by " + deviceID)
    devices.getOwnerID(deviceID).map{os =>
      os.headOption match {
        case Some(o) =>
          val zone: String = getUserCurrentZone(o)
          devices.getDeviceZone(deviceID).map{ zID =>
            if(zID.head != -1)
            {
                zones.getName(zID.head).map{ zName =>
                  if(zName != zone)
                  {
                    scriptRunner.runScript(o, "motion")
                  }
                  else
                  {
                    println(zName + "!=" + zone)
                  }
                }
            }
          }
        case None =>
      }
    }
    Future.successful(Ok)
  }

  private def getUserCurrentZone(username: String): String = {
    val request: WSRequest = ws.url("localhost:8000/location?group=" + username + "&user=" + username)
    request.get().map(response =>
      if(response.status == 200)
        return (response.json \ "users" \ username \ "location").as[String]
    )
    ""
  }

  def assignZone(deviceID: String): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val zone: Int = (request.body \ "zone").as[Int]
    devices.assignZone(deviceID, request.user.userID, zone).map(_ =>
      Ok
    )
  }

  def setIdealTemp(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val temp = (request.body \ "temp").as[Double]
    users.setIdealTemp(request.user.userID, temp).map(_ => Ok)
  }

  def getIdealTemp: Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    users.getIdealTemp(request.user.userID).map { ts =>
      ts.headOption match {
        case Some(t) => Ok(Json.obj("temp" -> t))
        case None => BadRequest
      }
    }
  }

  private def compareTemp(userID: String, value: Double): Unit = {
    users.getIdealTemp(userID).map { ts =>
      ts.headOption match {
        case Some(t) =>
          if (value + 2 < t) {
            actuators.sendToUserBridge(userID, "temperature low")
          }
          else if (value - 2 > t) {
          actuators.sendToUserBridge(userID, "temperature high")
          }
          else {
            actuators.sendToUserBridge(userID, "temperature normal")
          }

        case None =>
      }
    }
  }
}
