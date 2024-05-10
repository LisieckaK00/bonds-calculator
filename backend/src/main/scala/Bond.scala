import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

import scala.math.floor

case class Bond @JsonCreator() (
   @JsonProperty("name") name: String,
   @JsonProperty("yearPercentage") yearPercentage: Double,
   @JsonProperty("inflationModifier") inflationModifier: Double,
   @JsonProperty("capitalization") capitalization: Int,
   @JsonProperty("duration") duration: Int,
   @JsonProperty("penalty") penalty: Double,
   @JsonProperty("quantity") quantity: Int,
   @JsonProperty("type") bond_type: String,
   @JsonProperty("distribution") distribution: Option[Int],
   @JsonProperty("startPrice") startPrice: Double,
   @JsonProperty("change") change: Double
 ) {

  val inflation: Double = 5

  private def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def makeDecimalPercentage(x: Double): Double = {
    x/100
  }

  def calculate(period: Int): Unit = {
    bond_type match {
      case "acc" => calculateEndValueAcc(period)
      case "dist" => println("Function does not exist yet")
      case _ => println("Invalid type")

    }
  }

  private def calculateEndValueAcc(period: Int): Unit = {
    val result: Array[Array[Double]] = Array.fill[Double](period, 11)(-1.0)

    result(0)(0) = 1
    result(0)(2) = quantity
    result(0)(3) = startPrice
    result(0)(5) = 100
    result(0)(10) = 0

    for row <- 0 until period do
      for col <- 0 until 11 do
        if result(row)(col) == -1.0 then
            result(row)(col) = col match {
            case 0 => result(row - 1)(col) + 1
            case 1 => if result(row)(0) % duration == 0 then 1 else 0
            case 2 => if result(row)(1) == 1 then floor(result(row-1)(9) / change) else result(row-1)(2)
            case 3 => if result(row)(1) == 1 then result(row)(2) * change else result(row-1)(3)
            case 4 => result(row)(2) * startPrice
            case 5 => if result(row-1)(1) == 1 then result(row)(2) * result(row)(4)
                      else
                        if result(row)(0) % capitalization != 1
                        then result(row-1)(5)
                        else result(row-1)(7)
            case 6 => if inflationModifier != 0 then
                        if result(row)(0) > capitalization
                        then makeDecimalPercentage(inflation + inflationModifier)
                        else makeDecimalPercentage(yearPercentage)
                      else makeDecimalPercentage(yearPercentage)
            case 7 =>
              val multiplier = if (result(row)(0) % capitalization != 0) then result(row)(0) % capitalization
              else capitalization / 1.0
              result(row)(5) * (1 + (result(row)(6) * multiplier / 12.0))
            case 8 => if result(row)(1) == 1 then 0 else result(row)(7) - Math.max(result(row)(7) - penalty, startPrice)
            case 9 => result(row)(7) - result(row)(8) - (result(row)(7) - result(row)(8) - result(row)(2) * result(row)(3)) * 0.19
            case 10 => if (row > 2 && result(row)(1) == 1) then
                        result(row)(9) - math.floor(result(row)(9) / (result(row)(2) * result(row)(3))) * result(row)(2) * result(row)(3) + result(row - 1)(10)
                        else result(row - 1)(10)

            }

    for (row <- result) {
      for (elem <- row) {
        print(elem + " ")
      }
      println()
    }

  }
}
