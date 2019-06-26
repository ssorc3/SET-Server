package models

import play.api.http
import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class Zone(id: Int, userID: String, name: String)

object Zone {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[Zone]] = {
    http.Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[Zone]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[Zone] = Json.format[Zone]
}