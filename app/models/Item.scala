package models

import play.api.libs.json.{Json, OFormat}

case class Item(
                 id: String,
                 etag: String
               )

object Item {
  implicit val format: OFormat[Item] = Json.format[Item]
}
