package model

import play.api.libs.json.{Json, OFormat}

case class Params(quantity: Int, period: Int, inflation: List[Double]){
  val listString = inflation.mkString("-")
  def generateCacheKey(bondName: String): String = s"bond-$bondName-$quantity-$period-$listString"
}

// Companion object for Params containing JSON formatting
object Params {
  // JSON formatter for Params
  implicit val paramsFormat: OFormat[Params] = Json.format[Params]
}