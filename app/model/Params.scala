package model

import play.api.libs.json.{Json, OFormat}

/**
 * Represents parameters used in bond calculations.
 *
 * @param quantity  The quantity of bonds.
 * @param period    The period for which calculations are performed.
 * @param inflation The list of inflation rates for each period.
 */
case class Params(quantity: Int, period: Int, inflation: List[Double]){
  private val listString = inflation.mkString("-")

  /**
   * Generates a cache key based on the parameters and bond name.
   *
   * @param bondName The name of the bond.
   * @return A cache key string.
   */
  def generateCacheKey(bondName: String): String = s"bond-$bondName-$quantity-$period-$listString"
}

object Params {
  /**
   * Implicit JSON format for serializing and deserializing Params objects.
   */
  implicit val paramsFormat: OFormat[Params] = Json.format[Params]
}