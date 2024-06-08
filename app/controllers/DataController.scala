package controllers

import model.{BondList, JSONReader}
import play.api.cache.AsyncCacheApi
import play.api.libs.json.*
import play.api.mvc.*

import javax.inject.Inject
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

/**
 * DataController handles the retrieval and processing of bond data.
 * It provides endpoints to get properties, detailed information, and results for all bonds.
 * Data return from this controller rely on parameters provided by [[ParamsController]].
 *
 * @param cc               ControllerComponents for Play Framework.
 * @param cache            Asynchronous cache for storing bond calculation results.
 * @param paramsController Controller that provides access to stored parameters.
 * @param ec               ExecutionContext for managing asynchronous operations.
 */
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

  /**
   * Retrieves properties of all bonds.
   *
   * @return A JSON response with a list of bond properties.
   */
  def getAllBondsProperties = Action {
    val result: List[Map[String, Any]] = bondListFromFile.bonds.map(_.getProperties)
    val json = Json.toJson(result)
    Ok(json)
  }

  /**
   * Retrieves detailed information about a bond by its name.
   *
   * @param bondName The name of the bond.
   * @return A JSON response with bond details or an error if parameters are not set.
   */
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

  /**
   * Retrieves detailed information about all bonds.
   *
   * @return A JSON response with all bond details or an error if parameters are not set.
   */
  def getAllBonds = Action.async {
    val storedParams = paramsController.getStoredParams

    storedParams match {
      case Some(params) =>
        val futures = bondListFromFile.bonds.map { bond =>
          val cacheKey = params.generateCacheKey(bond.name)
          cache.getOrElseUpdate(cacheKey, 10.minutes) {
            Future.successful(bond.calculateEndValue(params)).map(result => Json.toJson(result))
          }.map(json => bond.name -> json)
        }

        Future.sequence(futures).map { bondResults =>
          val allBondsData = bondResults.toMap
          Ok(Json.toJson(allBondsData))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Parameters not set")))
    }
    
  }

  /**
   * Retrieves the final results for all bonds.
   *
   * @return A JSON response with the final bond results or an error if parameters are not set.
   */
  def getAllBondsFinalResult = Action.async {
    val storedParams = paramsController.getStoredParams

    storedParams match {
      case Some(params) =>
        val futures = bondListFromFile.bonds.map { bond =>
          val cacheKey = params.generateCacheKey(bond.name)
          cache.getOrElseUpdate(cacheKey, 10.minutes) {
            Future.successful(bond.calculateEndValue(params)).map(result => Json.toJson(result))
          }.map { cachedJson =>
            val maybeBondData = cachedJson.asOpt[Array[Array[Double]]]
            val maybeLastArray = maybeBondData.flatMap(_.lastOption) // Extract the last array if it exists
            val lastElement = maybeLastArray.flatMap(_.lastOption).getOrElse(0.0) // Get the last element of the last array or 0.0 if not found
            bond.name -> Json.toJson(lastElement)
          }
        }

        Future.sequence(futures).map { bondResults =>
          val allBondsData = bondResults.toMap
          Ok(Json.toJson(allBondsData))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Parameters not set")))
    }
  }
}




