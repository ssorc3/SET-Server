package models

import play.api.libs.json.{Json, Writes}

case class User(userID: String, username: String, hash: String)

object User {
  implicit val jsonWrites: Writes[User] = Json.writes[User]
}
