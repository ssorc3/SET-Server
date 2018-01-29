package controllers

import javax.inject._

import auth._
import models.{DeviceRepository, UserRepository}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class CreateUserForm(username: String, password: String)

@Singleton
class UserController @Inject()(cc: ControllerComponents, jwtUtil: JWTUtil, repo: UserRepository)(implicit ec: ExecutionContext) extends AbstractController(cc)
{
  def createUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    repo.isValidAsync(username, password).flatMap{
      case false =>
        repo.create(username, password).map(token =>
          Ok(token)
        )
      case true => Future.successful(BadRequest("Username already in use"))
    }
  }


  def getToken: Action[JsValue] = Action.async(parse.json) { implicit request =>
    val username = (request.body \ "username").toOption.fold("")(_.as[String])
    val password = (request.body \ "password").toOption.fold("")(_.as[String])
    repo.isValidAsync(username, password).map {
      case true => Ok(jwtUtil.createToken(Json.stringify(Json.obj(
        "username" -> username,
        "password" -> password
      ))))
      case false => Unauthorized("Invalid Credentials")
    }
  }
}
