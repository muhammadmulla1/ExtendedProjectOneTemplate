package models

import play.api.libs.json.{Json, OFormat}

case class DataModel(
                      id: String,
                      title: String,
                      author: String,
                      publishedYear: Int
                    )

object DataModel {
  implicit val format: OFormat[DataModel] = Json.format[DataModel]
}
