package model

import play.api.libs.json.{Json, OFormat}

case class Params(quantity: Int, period: Int){
  def generateCacheKey(bondName: String): String = s"bond-$bondName-$quantity-$period"
}

// Companion object for Params containing JSON formatting
object Params {
  // JSON formatter for Params
  implicit val paramsFormat: OFormat[Params] = Json.format[Params]
}