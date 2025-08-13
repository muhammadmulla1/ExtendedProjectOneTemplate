package models

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(
                       title: String,
                       authors: Option[Seq[String]],
                       publisher: Option[String],
                       publishedDate: Option[String]
                     )

object VolumeInfo {
  implicit val format: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}

case class BookItem(
                     id: String,
                     volumeInfo: VolumeInfo
                   )

object BookItem {
  implicit val format: OFormat[BookItem] = Json.format[BookItem]
}

case class GoogleBooksResponse(
                                kind: String,
                                totalItems: Int,
                                items: Seq[BookItem]
                              )

object GoogleBooksResponse {
  implicit val format: OFormat[GoogleBooksResponse] = Json.format[GoogleBooksResponse]
}
