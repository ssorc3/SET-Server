package repositories

import java.util.UUID
import javax.inject.Inject

import auth.JWTUtil
import models.{IdealTemp, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, auth: JWTUtil)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def userID = column[String]("userID", O.PrimaryKey)

    def username = column[String]("username", O.Unique)

    def hash = column[String]("hash", O.Length(80))

    def * = (userID, username, hash) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UserTable]

  def create(username: String, password: String): Future[String] = {
    val uuid = UUID.randomUUID.toString
    db.run {
      users += User(uuid, username, BCrypt.hashpw(password, BCrypt.gensalt))
    }.map(_ => auth.createToken(Json.stringify(Json.obj(
      "username" -> username,
      "password" -> password
    ))))
  }

  def list: Future[Seq[User]] = db.run {
    users.result
  }

  def userByID(id: String): Future[Option[User]] = db.run {
    users.filter(_.userID === id).result.map { us =>
      us.headOption
    }
  }

  def idByUsername(username: String): Future[Seq[String]] = db.run {
    users.filter(_.username === username).map(_.userID).result
  }

  def usernameByID(userID: String): Future[Option[String]] = db.run {
    users.filter(_.userID === userID).map(_.username).result.map(_.headOption)
  }

  def usernameExists(username: String): Future[Boolean] = db.run {
    users.filter(_.username === username).exists.result
  }

  def getUserID(username: String): Future[String] = {
    db.run(users.filter(_.username === username).result.headOption).map {
      case Some(user) => user.userID
      case None => ""
    }
  }

  def getAllUsers(): Future[Seq[User]] = db.run{
    users.result
  }

  def deleteUser(username: String): Future[Any] = db.run{
   users.filter(_.username === username).delete
  }

  def isValidAsync(username: String, password: String): Future[Boolean] = {
    getUsers(username).map { us =>
      us.headOption match {
        case Some(u) => BCrypt.checkpw(password, u.hash)
        case None => false
      }
    }
  }

  def getUsers(username: String): Future[Seq[User]] = db.run {
    users.filter(u => u.username === username).result
  }

  class IdealTempTable(tag: Tag) extends Table[IdealTemp](tag, "idealTemps")
  {
    def userID = column[String]("userID", O.PrimaryKey)
    def temp = column[Double]("temp")

    def * = (userID, temp) <> ((IdealTemp.apply _).tupled, IdealTemp.unapply)
  }

  def idealTemps = TableQuery[IdealTempTable]

  def setIdealTemp(userID: String, temp: Double): Future[Any] = {
    db.run(idealTemps.filter(_.userID === userID).delete)
    db.run(idealTemps += IdealTemp(userID, temp))
  }

  def getIdealTemp(userID: String): Future[Seq[Double]] = db.run {
    idealTemps.filter(_.userID === userID).map(_.temp).result
  }
}