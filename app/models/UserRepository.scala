package models

import java.util.UUID
import javax.inject.Inject

import auth.JWTUtil
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, auth: JWTUtil)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users")
  {
    def userID = column[String]("userID", O.PrimaryKey)
    def username = column[String]("username", O.Unique)
    def hash = column[String]("hash", O.Length(80))

    def * = (userID, username, hash) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UserTable]

  def create(username: String, password: String): Future[String] = {
    val uuid = UUID.randomUUID.toString
    db.run{users += User(uuid, username, BCrypt.hashpw(password, BCrypt.gensalt))}.map(_ => auth.createToken(Json.stringify(Json.obj(
            "username" -> username,
            "password" -> password
          ))))
  }

  def list(): Future[Seq[User]] = db.run{
    users.result
  }

  def userByID(id: String): Future[Option[User]] = db.run{
    users.filter(_.userID === id).result.map { us =>
      us.headOption match{
        case Some(u: User) => Some(u)
        case None => None
      }
    }
  }

  def idByUsername(username: String): Future[Seq[String]] = db.run{
    users.filter(_.username === username).map(_.userID).result
  }

  def isValid(username: String, password: String): Boolean = {
    val users = Await.result(getUsers(username), Duration.Inf)
    users.headOption match {
      case Some(u) => BCrypt.checkpw(password, u.hash)
      case None => false
    }
  }

  def getUserID(username: String): Future[String] = {
    db.run(users.filter(_.username === username).result.headOption).map{
      case Some(user) => user.userID
      case None => ""
    }
  }

  def isValidAsync(username: String, password: String): Future[Boolean] = {
    getUsers(username).map { us =>
      us.headOption match {
        case Some(u) => BCrypt.checkpw(password, u.hash)
        case None => false
      }
    }
  }

  def getUsers(username: String): Future[Seq[User]] = db.run{
    users.filter(u => u.username === username).result
  }

  def delete(): Future[Int] = db.run {
    users.delete
  }
}
