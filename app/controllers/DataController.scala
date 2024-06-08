package controllers

import model.{BondList, JSONReader}
import play.api.cache.AsyncCacheApi
import play.api.libs.json.*
import play.api.mvc.*

import javax.inject.Inject
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

class DataController @Inject()(cc: ControllerComponents, cache: AsyncCacheApi, paramsController: ParamsController)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  private val reader: JSONReader = new JSONReader()
  private val bondListFromFile: BondList = reader.loadFromFile("data.json")

  // Define implicit formats
  implicit val doubleArrayWrites: Writes[Array[Double]] = Writes.arrayWrites[Double]
  implicit val doubleArrayArrayWrites: Writes[Array[Array[Double]]] = Writes.arrayWrites[Array[Double]]
  implicit val mapWrites: Writes[Map[String, Any]] = new Writes[Map[String, Any]] {
    def writes(map: Map[String, Any]): JsValue = {
      Json.obj(map.map { case (key, value) => key -> (value match {
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

  def getAllBondsProperties = Action {
    val result: List[Map[String, Any]] = bondListFromFile.bonds.map(_.getProperties)
    val json = Json.toJson(result)
    Ok(json)
  }

  def getByName(bondName: String) = Action.async {
    val storedParams = paramsController.getStoredParams

    storedParams match {
      case Some(params) =>
        val cacheKey = params.generateCacheKey(bondName)
        cache.getOrElseUpdate[JsValue](cacheKey, 10.minutes) {
          val result: Option[Array[Array[Double]]] = bondListFromFile
            .bonds
            .find(_.name == bondName)
            .map(_.calculateEndValue(params))
          val resultValue: Array[Array[Double]] = result.getOrElse(Array.empty[Array[Double]])

          Future.successful(Json.toJson(resultValue))
        }.map(cachedData =>
          val json = Json.obj(bondName -> cachedData)
          Ok(json))
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Parameters not set")))
    }
  }

  def getAllBonds = Action.async {
    val storedParams = paramsController.getStoredParams

    storedParams match {
      case Some(params) =>
        val futures = bondListFromFile.bonds.map { bond =>
          val cacheKey = params.generateCacheKey(bond.name)
          cache.getOrElseUpdate(cacheKey, 10.minutes) {
            Future.successful(bond.calculateEndValue(params)).map(result => Json.toJson(result)) // Serialize result to JSON
          }.map(json => bond.name -> json)
        }

        Future.sequence(futures).map { bondResults =>
          val allBondsData = bondResults.toMap
          Ok(Json.toJson(allBondsData)) // Convert the result to JSON
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Parameters not set")))
    }
    
  }

  def getAllBondsFinalResult = Action.async {
    val storedParams = paramsController.getStoredParams

    storedParams match {
      case Some(params) =>
        val futures = bondListFromFile.bonds.map { bond =>
          val cacheKey = params.generateCacheKey(bond.name)
          cache.getOrElseUpdate(cacheKey, 10.minutes) {
            Future.successful(bond.calculateEndValue(params)).map(result => Json.toJson(result)) // Serialize result to JSON
          }.map { cachedJson =>
            val maybeBondData = cachedJson.asOpt[Array[Array[Double]]] // Convert JsValue to Array[Array[Double]]
            val maybeLastArray = maybeBondData.flatMap(_.lastOption) // Extract the last array if it exists
            val lastElement = maybeLastArray.flatMap(_.lastOption).getOrElse(0.0) // Get the last element of the last array or 0.0 if not found
            bond.name -> Json.toJson(lastElement) // Convert the last element to Json and return
          }
        }

        Future.sequence(futures).map { bondResults =>
          val allBondsData = bondResults.toMap
          Ok(Json.toJson(allBondsData)) // Convert the result to JSON
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Parameters not set")))
    }
  }  
}




