package controllers

import javax.inject.Inject
import play.api.mvc.*
import play.api.libs.json.*
import play.api.cache.AsyncCacheApi
import model.{BondList, JSONReader}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*

class DataController @Inject()(cc: ControllerComponents, cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  private val reader: JSONReader = new JSONReader()
  private val bondListFromFile: BondList = reader.loadFromFile("data.json")

  // Define implicit formats
  implicit val doubleArrayWrites: Writes[Array[Double]] = Writes.arrayWrites[Double]
  implicit val doubleArrayArrayWrites: Writes[Array[Array[Double]]] = Writes.arrayWrites[Array[Double]]

  implicit val mapWrites: Writes[Map[String, Any]] = new Writes[Map[String, Any]] {
    def writes(map: Map[String, Any]): JsValue = {
      Json.obj(map.map {
        case (key, value) =>
          key -> (value match {
            case v: String => Json.toJsFieldJsValueWrapper(v)
            case v: Int => Json.toJsFieldJsValueWrapper(v)
            case v: Double => Json.toJsFieldJsValueWrapper(v)
            case v: Float => Json.toJsFieldJsValueWrapper(v.toDouble)
            case v: Long => Json.toJsFieldJsValueWrapper(v)
            case v: Boolean => Json.toJsFieldJsValueWrapper(v)
            case v => Json.toJsFieldJsValueWrapper(v.toString)
          })
      }.toSeq: _*)
    }
  }

  def getAllBondsProperties() = Action {
    val result: List[Map[String, Any]] = bondListFromFile.bonds.map(_.getProperties)
    val json = Json.toJson(result)
    Ok(json)
  }

  def getByName(bondName: String, quantity: Int, period: Int) = Action.async {
    val cacheKey = s"bond-$bondName-$quantity-$period"
    cache.getOrElseUpdate[JsValue](cacheKey, 10.minutes) {
      val result: Option[Array[Array[Double]]] = bondListFromFile.bonds.find(_.name == bondName).map(_.calculateEndValue(quantity, period))
      val resultValue: Array[Array[Double]] = result.getOrElse(Array.empty[Array[Double]])

      Future.successful(Json.obj(bondName -> resultValue))
    }.map(cachedData =>
        val json = Json.obj(bondName -> cachedData)
        Ok(json))
  }

  def getAllBonds(quantity: Int, period: Int) = Action.async {
    val futures = bondListFromFile.bonds.map { bond =>
      val cacheKey = s"bond-${bond.name}-$quantity-$period"
      cache.getOrElseUpdate(cacheKey, 10.minutes) {
        Future.successful(bond.calculateEndValue(quantity, period))
          .map(result => Json.toJson(result)) // Serialize result to JSON
      }.map(json => bond.name -> json)
    }

    Future.sequence(futures).map { bondResults =>
      val allBondsData = bondResults.toMap
      Ok(Json.toJson(allBondsData)) // Convert the result to JSON
    }
  }

  def getAllBondsFinalResult(quantity: Int, period: Int) = Action.async {
    val futures = bondListFromFile.bonds.map { bond =>
      val cacheKey = s"bond-${bond.name}-$quantity-$period"
      cache.getOrElseUpdate(cacheKey, 10.minutes) {
        Future.successful(bond.calculateEndValue(quantity, period))
          .map(result => Json.toJson(result)) // Serialize result to JSON
      }.map { cachedJson =>
        val maybeBondData = cachedJson.asOpt[Array[Array[Double]]] // Convert JsValue to Array[Array[Double]]
        val maybeLastArray = maybeBondData.flatMap(_.lastOption) // Extract the last array if it exists
        val lastArray = maybeLastArray.getOrElse(Array.empty[Double]) // Get the last array or an empty array if not found
        bond.name -> Json.toJson(lastArray) // Convert the last array to Json and return
      }
    }

    Future.sequence(futures).map { bondResults =>
      val allBondsData = bondResults.toMap
      Ok(Json.toJson(allBondsData)) // Convert the result to JSON
    }
  }


}




