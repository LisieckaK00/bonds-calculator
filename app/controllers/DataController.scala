package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json._

import model.{ BondList, JSONReader}

class DataController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Definiowanie implicitnych formatów
  implicit val doubleArrayWrites: Writes[Array[Double]] = Writes.arrayWrites[Double]
  implicit val doubleArrayArrayWrites: Writes[Array[Array[Double]]] = Writes.arrayWrites[Array[Double]]

  def getData = Action { implicit request: Request[AnyContent] =>
    val randomData = Seq("RandomData1", "RandomData2", "RandomData3")
    val json = Json.obj(
      "data" -> randomData
    )

    Ok(json)
  }

  def getByName(bondName: String, period: Int) = Action {
    val reader: JSONReader = new JSONReader()
    val bondListFromFile: BondList = reader.loadFromFile("data.json")
    val result: Option[Array[Array[Double]]] = bondListFromFile.bonds.find(_.name == bondName).map(_.calculateEndValue(period))
    val resultValue: Array[Array[Double]] = result.getOrElse(Array.empty[Array[Double]])
    val json = Json.toJson(resultValue)
    Ok(json)



  }
}




