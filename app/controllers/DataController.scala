package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json._

import model.{ BondList, JSONReader}

class DataController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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

  def getByName(bondName: String, quantity: Int, period: Int) = Action {
    val reader: JSONReader = new JSONReader()
    val bondListFromFile: BondList = reader.loadFromFile("data.json")
    val result: Option[Array[Array[Double]]] = bondListFromFile.bonds.find(_.name == bondName).map(_.calculateEndValue(quantity, period))
    val resultValue: Array[Array[Double]] = result.getOrElse(Array.empty[Array[Double]])

    val json = Json.obj(bondName -> resultValue)
    Ok(json)
  }

  def getAllBondsProperties() = Action {
    val reader: JSONReader = new JSONReader()
    val bondListFromFile: BondList = reader.loadFromFile("data.json")
    val result: List[Map[String, Any]] = bondListFromFile.bonds.map(_.getProperties)
    val json = Json.toJson(result)
    Ok(json)
  }

  def getAllBonds(quantity: Int, period: Int) = Action {
    val reader: JSONReader = new JSONReader()
    val bondListFromFile: BondList = reader.loadFromFile("data.json")

    val allBondsData: Map[String, Array[Array[Double]]] = bondListFromFile.bonds.map { bond =>
      bond.name -> bond.calculateEndValue(quantity, period)
    }.toMap

    val json = Json.toJson(allBondsData)
    Ok(json)
  }
  
  def getAllBondsFinalResult(quantity: Int, period: Int) = Action {
    val reader: JSONReader = new JSONReader()
    val bondListFromFile: BondList = reader.loadFromFile("data.json")

    val allFinalResult: Map[String, Array[Double]] = bondListFromFile.bonds.map { bond =>
      bond.name -> bond.calculateEndValue(quantity, period).map(row => row.last)
    }.toMap

    val json = Json.toJson(allFinalResult)
    Ok(json)
  }
}




