package repositories

import javax.inject.Inject

import models.Script
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ScriptRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class ScriptTable(tag: Tag) extends Table[Script](tag, "scripts") {
    val userID = column[String]("userID", O.PrimaryKey)
    val scriptName = column[String]("scriptName")
    val script = column[String]("script")
    val lastRun = column[Long]("lastRun")

    def * = (userID, scriptName, script, lastRun) <> ((Script.apply _).tupled, Script.unapply)
  }

  val scripts = TableQuery[ScriptTable]

  def setUserScript(userID: String, scriptName:String, script: String): Future[Any] = {
    db.run(scripts.filter(s => s.userID === userID && s.scriptName === scriptName).delete)
    db.run(scripts += Script(userID, scriptName, script, 0))
  }

  def updateLastRun(userID: String, lastRun: Long): Future[Any] = {
    db.run(scripts.filter(_.userID === userID).map(_.lastRun).update(lastRun))
  }

  def getUserScript(userID: String, scriptName: String): Future[Seq[String]] = db.run {
    scripts.filter(s => s.userID === userID && s.scriptName === scriptName).map(_.script).result
  }

  def getUserScripts(userID: String): Future[Seq[Script]] = db.run {
    scripts.filter(_.userID === userID).result
  }

  def getUserLastRun(userID: String): Future[Seq[Long]] = db.run {
    scripts.filter(_.userID === userID).map(_.lastRun).result
  }
}