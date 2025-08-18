package models

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(
                       title: String,
                       authors: Option[Seq[String]]
                     )

object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}

case class BookItem(
                     id: String,
                     etag: String,
                     volumeInfo: VolumeInfo
                   )

object BookItem {
  implicit val format: OFormat[BookItem] = Json.format[BookItem]
}

case class BookResponse(
                         kind: String,
                         totalItems: Int,
                         items: Seq[BookItem]
                       )

object BookResponse {
  implicit val format: OFormat[BookResponse] = Json.format[BookResponse]
}
