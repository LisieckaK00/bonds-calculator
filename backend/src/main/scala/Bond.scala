import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

case class Bond @JsonCreator() (
   @JsonProperty("name") name: String,
   @JsonProperty("yearPercentage") yearPercentage: Double,
   @JsonProperty("capitalization") capitalization: Int,
   @JsonProperty("duration") duration: Int,
   @JsonProperty("penalty") penalty: Double,
   @JsonProperty("quantity") quantity: Int,
   @JsonProperty("type") bond_type: String,
   @JsonProperty("distribution") distribution: Option[Int]

 ) {
  val startValue: Double = 100 * quantity
  val monthPercentage: Double = yearPercentage / 12


  def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def calculate(period: Int): Array[Array[Double]] = {
    val result: Array[Array[Double]] = Array.ofDim[Double](period, 5)

    for i <- 1 to period do
      val (baseValue, currentValue, monthInterest, currentValueWithPenalty, accountValue) = calculateEndValue(i)
      result(i - 1) = Array(
        roundToTwoPlaces(baseValue),
        roundToTwoPlaces(currentValue),
        roundToTwoPlaces(monthInterest),
        roundToTwoPlaces(currentValueWithPenalty),
        roundToTwoPlaces(accountValue)
      )

    bond_type match {
      case "acc" => {
        println(s"Obligacja: $name")
        println("End of Month | Base Value | Current Value | Month Interest | Value After Penalty | Account Value")
      }
      case "dist" => {
        println(s"Obligacja: $name")
        println("End of Month | Base Value | Current Value |     Withdrawal | Value After Penalty | Account Value")
      }
    }

    result.zipWithIndex.foreach { case (row, index) =>
      val month = index + 1
      println(f"$month%12d | ${row(0)}%10.2f | ${row(1)}%13.2f | ${row(2)}%14.2f | ${row(3)}%19.2f | ${row(4)}%13.2f")
    }

    result
  }

  def calculateEndValue(period: Int): (Double, Double, Double, Double, Double) = {
    var baseValue = startValue
    var currentValue: Double = startValue
    var cumulativeInterest: Double = 0
    var monthInterest: Double = 0
    var accountValue: Double = 0

    for i <- 1 to period do
      monthInterest = baseValue * monthPercentage
      cumulativeInterest += monthInterest

      bond_type match {
        case "acc" => {
          currentValue += monthInterest
          if i % capitalization == 0 then
            baseValue += cumulativeInterest
            cumulativeInterest = 0

          if period % duration == 0 then
             accountValue = currentValue

        }
        case "dist" => {
          if distribution.get == 1 | i % distribution.get == 0 then
            accountValue += cumulativeInterest
            cumulativeInterest = 0
        }
      }



    var currentValueWithPenalty = currentValue

    if period % duration != 0 then
      currentValueWithPenalty = math.max(currentValue - penalty, startValue)

    (baseValue, currentValue, monthInterest, currentValueWithPenalty, accountValue)
  }
}
