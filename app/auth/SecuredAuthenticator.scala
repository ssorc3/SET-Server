package auth

import javax.inject.Inject

import models.UserRepository
import play.api.mvc._
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

case class User(username: String, password: String)
case class UserInfo(username: String, userID: String)

case class UserRequest[A](user: UserInfo, request: Request[A]) extends WrappedRequest(request)

class SecuredAuthenticator @Inject()(jwtUtil: JWTUtil, repo: UserRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val formatUser: OFormat[User] = Json.format[User]

  object JWTAuthentication extends ActionBuilder[UserRequest, AnyContent]
  {
    override protected def executionContext: ExecutionContext = cc.executionContext
    override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

    def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
      val jwtToken = request.headers.get("jw_token").getOrElse("")

      if(jwtUtil.isValidToken(jwtToken)){
        jwtUtil.decodePayload(jwtToken).fold {
          Future.successful(Unauthorized("Invalid Credentials"))
        }
        { payload =>
          val userCredentials = Json.parse(payload).validate[User].get
          repo.isValidAsync(userCredentials.username, userCredentials.password).flatMap{
            case true =>
              repo.getUserID(userCredentials.username).flatMap{id =>
                block(UserRequest(UserInfo(userCredentials.username, id), request))
              }
            case false => Future.successful(Unauthorized("Invalid Credentials"))
          }
        }
      }
      else
        Future.successful(Unauthorized("Invalid Credentials"))
    }
  }
}
