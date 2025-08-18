package models

import play.api.libs.json.{Json, OFormat}

case class Book(
                 id: String,
                 etag: String,
                 name: String,
                 description: String,
                 pageCount: Int
               )

object Book {
  implicit val format: OFormat[Book] = Json.format[Book]
}
