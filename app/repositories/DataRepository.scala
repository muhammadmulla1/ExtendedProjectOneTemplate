package repositories

import models.DataModel
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model.{IndexModel, Indexes, ReplaceOptions}
import org.mongodb.scala.result.DeleteResult

import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.format,  // <-- your DataModel JSON format name, note singular "format"
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[Int, Seq[DataModel]]] =
    collection.find().toFuture().map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(404)
    }

  def create(book: DataModel): Future[DataModel] =
    collection.insertOne(book).toFuture().map(_ => book)

  private def byID(id: String): Bson =
    Filters.equal("_id", id)

  def read(id: String): Future[Option[DataModel]] =
    collection.find(byID(id)).headOption()

  def update(id: String, book: DataModel): Future[Option[DataModel]] = {
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(false)
    ).toFuture().flatMap { updateResult =>
      if (updateResult.getMatchedCount > 0) {
        collection.find(byID(id)).headOption()
      } else {
        Future.successful(None)
      }
    }
  }

  def delete(id: String): Future[DeleteResult] =
    collection.deleteOne(filter = byID(id)).toFuture()

  def deleteAll(): Future[Unit] =
    collection.deleteMany(empty()).toFuture().map(_ => ())

}
