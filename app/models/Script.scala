package models

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class Script(userID: String, scriptName: String, script: String, lastRun: Long)

object Script{
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[Script]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[Script]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val scriptFormat: OFormat[Script] = Json.format[Script]
}