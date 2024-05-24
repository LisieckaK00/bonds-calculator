package model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import scala.Array
import scala.math.floor

case class Bond @JsonCreator() (
   @JsonProperty("name") name: String,
   @JsonProperty("percentage") percentage: Double,
   @JsonProperty("duration") duration: Double,
   @JsonProperty("capitalization") capitalization: Int,
   @JsonProperty("price") price: Int,
   @JsonProperty("change") change: Double,
   @JsonProperty("penalty") penalty: Double,
   @JsonProperty("multiplier") multiplier: Double,
   @JsonProperty("multiplierActivation") multiplierActivation: Int,
   @JsonProperty("type") bond_type: String,
 ) {

  val inflation: Double = 5

  private def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def makeDecimalPercentage(percentage: Double): Double = {
    BigDecimal(percentage / 100).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble
  }


  // yes, I know it's pretty bad, but it works somehow
  var monthsArray: Array[Int] = _
  var quantityArray: Array[Int] = _
  var buyPriceArray: Array[Double] = _
  var basePriceArray: Array[Double] = _
  var percentageArray: Array[Double] = _
  var grossValueArray: Array[Double] = _
  var penaltyArray: Array[Double] = _
  var withdrawalArray: Array[Double] = _
  var accountArray: Array[Double] = _
  var finalResultArray: Array[Double] = _

  def initializeArrays(period: Int): Unit = {
    monthsArray = Array.ofDim[Int](period)
    quantityArray = Array.fill[Int](period)(0)
    buyPriceArray = Array.fill[Double](period)(0.0)
    basePriceArray = Array.fill[Double](period)(0.0)
    percentageArray = Array.fill[Double](period)(0.0)
    grossValueArray = Array.fill[Double](period)(0.0)
    penaltyArray = Array.fill[Double](period)(0.0)
    withdrawalArray = Array.fill[Double](period)(0.0)
    accountArray = Array.fill[Double](period)(0.0)
    finalResultArray = Array.fill[Double](period)(0.0)
  }

  private def calculatePercentage(month: Int): Unit = {
    percentageArray(month) = if multiplier == 0 || month + 1 <= multiplierActivation  then
      makeDecimalPercentage(percentage)
    else
      makeDecimalPercentage(inflation +  multiplier)
  }
  
  private def calculateGrossValue(month: Int): Unit = {
    val currentMultiplier = if (month + 1) % 12 != 0 then (month + 1) % 12 else 12
    grossValueArray(month) = basePriceArray(month) * (1 + (percentageArray(month) * currentMultiplier) / 12 )
  }

  private def calculatePenalty(month: Int): Unit = {
    penaltyArray(month) = if month + 1 % duration == 0 then
      0
    else Math.min(quantityArray(month) * penalty, grossValueArray(month) - quantityArray(month) * penalty)
  }

  private def calculateWithdrawal(month: Int): Unit = {
    withdrawalArray(month) = Math.max(quantityArray(month) * buyPriceArray(month) +
      (grossValueArray(month) - penaltyArray(month) - quantityArray(month) * buyPriceArray(month)) * 0.81,
      quantityArray(month) * price)
  }

  private def calculateAccount(month: Int): Unit = {
    val temp_val = quantityArray(month - 1) * price + (withdrawalArray(month - 1)
      - (quantityArray(month - 1) * price) ) + accountArray(month - 1)

    accountArray(month) = if month % duration == 0 then
      temp_val - (floor(temp_val / change).toInt * change)
    else
      accountArray(month - 1)
  }

  private def calculateFinalResult(month: Int): Unit = {
    finalResultArray(month) = withdrawalArray(month) + accountArray(month)
  }

  private def calculateQuantity(month: Int): Unit = {
    quantityArray(month) = if month % duration == 0 then
      floor((withdrawalArray(month - 1) + accountArray(month - 1)) / change).toInt
    else quantityArray(month - 1)
  }

  private def calculateBuyPrice(month: Int): Unit = {
    buyPriceArray(month) = if month % duration == 0 then change else buyPriceArray(month - 1)
  }

  private def calculateBasePrice(month: Int): Unit = {
    basePriceArray(month) = if month % duration == 0 then
      quantityArray(month) * price
    else if month % capitalization == 0 then
      grossValueArray(month - 1)
    else basePriceArray(month - 1)
  }


  def calculateEndValue(period: Int): Array[Double] = {
    initializeArrays(period)

    // temporary values, some of them are necessary
    quantityArray(0) = 100
    buyPriceArray(0) = 100
    basePriceArray(0) = 10000
    percentageArray(0) = 0.064
    grossValueArray(0) = 10000.53
    penaltyArray(0) = 53.333
    withdrawalArray(0) = 10000
    accountArray(0) = 0
    finalResultArray(0) = 10000

    monthsArray.indices.foreach(i => monthsArray(i) = i + 1)

    for month <- 1 until period do
      calculatePercentage(month)
      calculateBuyPrice(month)
      calculateQuantity(month)
      calculateBasePrice(month)
      calculateGrossValue(month)
      calculatePenalty(month)
      calculateWithdrawal(month)
      calculateAccount(month)
      calculateFinalResult(month)

    println(f"Month | Quantity | BuyPrice | BasePrice | Percentage | GrossValue | Penalty | Withdrawal | Account | FinalResult")
    for month <- 0 until period do
      println(f"${monthsArray(month)}%6d | ${quantityArray(month)}%8d | ${buyPriceArray(month)}%8.2f | ${basePriceArray(month)}%9.2f | ${percentageArray(month)}%10.4f | ${grossValueArray(month)}%10.2f | ${penaltyArray(month)}%7.2f | ${withdrawalArray(month)}%10f | ${accountArray(month)}%7.2f | ${finalResultArray(month)}%11.2f")

    finalResultArray
  }


}

