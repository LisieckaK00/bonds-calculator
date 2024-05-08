class Bond(val name: String, val yearPercentage: Double, val capitalization: Int, val duration: Int, val penalty: Double, val quantity: Int) {
  val startValue: Double = 100 * quantity;
  val monthPercentage: Double = yearPercentage / 12

  def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def calculate(period: Int): Array[Array[Double]] = {
    val result: Array[Array[Double]] = Array.ofDim[Double](period, 4)

    for i <- 1 to period do
      val (baseValue, currentValue, monthInterest, currentValueWithPenalty) = calculateEndValue(i)
      result(i - 1) = Array(
        roundToTwoPlaces(baseValue),
        roundToTwoPlaces(currentValue),
        roundToTwoPlaces(monthInterest),
        roundToTwoPlaces(currentValueWithPenalty)
      )

    println("End of Month | Base Value | Current Value | Month Interest | Value After Penalty")
    result.zipWithIndex.foreach { case (row, index) =>
      val month = index + 1
      println(f"$month%12d | ${row(0)}%10.2f | ${row(1)}%13.2f | ${row(2)}%14.2f | ${row(3)}%19.2f")
    }

    result
  }

  def calculateEndValue(period: Int): (Double, Double, Double, Double) = {
    var baseValue = startValue
    var currentValue: Double = startValue
    var cumulativeInterest: Double = 0
    var monthInterest: Double = 0;


    for i <- 1 to period do
      monthInterest = baseValue * monthPercentage
      cumulativeInterest += monthInterest
      currentValue += monthInterest

      if i % capitalization == 0 then
        baseValue = baseValue + cumulativeInterest
        cumulativeInterest = 0

    var currentValueWithPenalty = currentValue

    if period % duration != 0 then
      currentValueWithPenalty = math.max(currentValue - penalty, startValue)

    return (baseValue, currentValue, monthInterest, currentValueWithPenalty)
  }


}
