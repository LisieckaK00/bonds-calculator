package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json._

class DataController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getData = Action { implicit request: Request[AnyContent] =>
    val randomData = generateRandomData()
    val json = Json.obj(
      "data" -> randomData
    )

    Ok(json)
  }

  private def generateRandomData(): Seq[String] = {
    Seq("RandomData1", "RandomData2", "RandomData3")
  }
}




