package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ScriptRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class ScriptTable(tag: Tag) extends Table[Script](tag, "scripts") {
    val userID = column[String]("userID", O.PrimaryKey)
    val script = column[String]("script")

    def * = (userID, script) <> ((Script.apply _).tupled, Script.unapply)
  }

  val scripts = TableQuery[ScriptTable]

  def setUserScript(userID: String, script: String): Future[Any] = db.run {
    scripts.filter(_.userID === userID).delete
    scripts += Script(userID, script)
  }

  def getUserScript(userID: String): Future[Seq[String]] = db.run {
    scripts.filter(_.userID === userID).map(_.script).result
  }
}