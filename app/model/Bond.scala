package model

import scala.math.floor

trait Bond {
  val name: String
  val percentage: Double
  val duration: Double
  val price: Int
  val change: Double
  val penalty: Double
  val multiplier: Double
  val multiplierActivation: Int
  val description: String

  val inflation: Double = 4

  //  PRIVATE
  private def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def makeDecimalPercentage(percentage: Double): Double = {
    BigDecimal(percentage / 100).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def calculatePercentage(month: Int, result: Result): Unit = {
    result.percentageArray(month) = 
      if (multiplier == 0 || (month % duration) < multiplierActivation) {
        makeDecimalPercentage(percentage)
      } else {
        makeDecimalPercentage(inflation + multiplier)
      }
  }

  private def calculatePenalty(month: Int, result: Result): Unit = {
    result.penaltyArray(month) = 
      if ((month + 1) % duration == 0) {
        0
      } else {
        Math.min(result.quantityArray(month) * penalty, result.grossValueArray(month) - result.quantityArray(month) * price)
      }
  }

  private def calculateFinalResult(month: Int, result: Result): Unit = {
    result.finalResultArray(month) = result.withdrawalArray(month) + result.accountArray(month)
  }

  private def calculateQuantity(month: Int, result: Result): Unit = {
    result.quantityArray(month) = 
      if (month % duration == 0) {
        floor((result.withdrawalArray(month - 1) + result.accountArray(month - 1)) / change).toInt
      } else {
        result.quantityArray(month - 1)
      }
  }

  private def calculateBuyPrice(month: Int, result: Result): Unit = {
    result.buyPriceArray(month) = if (month % duration == 0) change else result.buyPriceArray(month - 1)
  }

  private def getInitialisedRecord(quantity: Int, period: Int): Result = {
    val result = new Result(period)

    result.quantityArray(0) = quantity
    result.buyPriceArray(0) = price
    result.basePriceArray(0) = quantity * price
    calculatePercentage(0, result)
    calculateGrossValue(0, result)
    calculatePenalty(0, result)
    calculateWithdrawal(0, result)
    calculateAccountStartingValue(result)
    calculateFinalResult(0, result)

    result.monthsArray.indices.foreach(i => result.monthsArray(i) = i + 1)

    result
  }

  // PROTECTED
  protected def calculateAccountStartingValue(result: Result): Unit

  protected def calculateBasePrice(month: Int, result: Result): Unit

  protected def calculateGrossValue(month: Int, result: Result): Unit

  protected def calculateAccount(month: Int, result: Result): Unit

  protected def calculateWithdrawal(month: Int, result: Result): Unit

  // PUBLIC
  def getProperties: Map[String, Any] = {
    Map(
      "name" -> name,
      "percentage" -> percentage,
      "multiplier" -> multiplier,
      "multiplierActivation" -> multiplierActivation,
      "description" -> description
    )
  }

  def calculateEndValue(quantity: Int, period: Int): Array[Array[Double]] = {
    val result = getInitialisedRecord(quantity, period)

    for (month <- 1 until period) {
      calculatePercentage(month, result)
      calculateBuyPrice(month, result)
      calculateQuantity(month, result)
      calculateBasePrice(month, result)
      calculateGrossValue(month, result)
      calculatePenalty(month, result)
      calculateWithdrawal(month, result)
      calculateAccount(month, result)
      calculateFinalResult(month, result)
    }

    // test print

//    println(f"Month | Quantity | BuyPrice | BasePrice | Percentage | GrossValue | Penalty | Withdrawal | Account | FinalResult")
//    for (month <- 0 until period) {
//      println(f"${result.monthsArray(month)}%5d | ${result.quantityArray(month)}%8d | ${result.buyPriceArray(month)}%8.2f | ${result.basePriceArray(month)}%9.2f | ${result.percentageArray(month)}%10.4f | ${result.grossValueArray(month)}%10.2f | ${result.penaltyArray(month)}%7.2f | ${result.withdrawalArray(month)}%10.2f | ${result.accountArray(month)}%7.2f | ${result.finalResultArray(month)}%11.2f")
//    }

    result.mergeArrays()
  }
}
