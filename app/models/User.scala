package models

import play.api.http
import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class User(userID: String, username: String, hash: String)

object User {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[User]] = {
    http.Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[User]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[User] = Json.format[User]
}