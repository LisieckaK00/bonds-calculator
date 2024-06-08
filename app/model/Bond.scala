package model

import scala.math.floor

/**
 * Represents a bond with all necessary attributes and methods for calculations.
 */
trait Bond {
  /** The name of the bond. */
  val name: String

  /** The annual interest rate of the bond. */
  val percentage: Double

  /** The duration of the bond in months. */
  val duration: Double

  /** The price of the bond. */
  val price: Int

  /** The price of the bond exchange. */
  val change: Double

  /** The penalty for early redemption. */
  val penalty: Double

  /** The multiplier used in calculations. */
  val multiplier: Double

  /** The month when the multiplier activates. */
  val multiplierActivation: Int

  /** The description of the bond. */
  val description: String

  // PUBLIC

  /**
   * Gets the properties of the bond.
   *
   * @return A map of bond properties.
   */
  def getProperties: Map[String, Any] = {
    Map(
      "name" -> name,
      "percentage" -> percentage,
      "multiplier" -> multiplier,
      "multiplierActivation" -> multiplierActivation,
      "description" -> description
    )
  }

  /**
   * Calculates the end value of the bond based on given parameters.
   *
   * @param params The parameters for calculation.
   * @return A 2D array of doubles representing the end values.
   */
  def calculateEndValue(params: Params): Array[Array[Double]] = {
    val result = getInitialisedRecord(params.quantity, params.period, params.inflation)

    for (month <- 1 until params.period) {
      calculatePercentage(month, result, params.inflation)
      calculateBuyPrice(month, result)
      calculateQuantity(month, result)
      calculateBasePrice(month, result)
      calculateGrossValue(month, result)
      calculatePenalty(month, result)
      calculateWithdrawal(month, result)
      calculateAccount(month, result)
      calculateFinalResult(month, result)
    }

    result.mergeArrays()
  }

  // PROTECTED

  /**
   * Calculates the starting value of the account.
   *
   * @param result The result object to update.
   */
  protected def calculateAccountStartingValue(result: Result): Unit

  /**
   * Calculates the base price for a given month and updates the result.
   *
   * @param month  The month for which to calculate the base price.
   * @param result The result object to update.
   */
  protected def calculateBasePrice(month: Int, result: Result): Unit

  /**
   * Calculates the gross value for a given month and updates the result.
   *
   * @param month  The month for which to calculate the gross value.
   * @param result The result object to update.
   */
  protected def calculateGrossValue(month: Int, result: Result): Unit

  /**
   * Calculates the account value for a given month and updates the result.
   *
   * @param month  The month for which to calculate the account value.
   * @param result The result object to update.
   */
  protected def calculateAccount(month: Int, result: Result): Unit

  /**
   * Calculates the withdrawal for a given month and updates the result.
   *
   * @param month  The month for which to calculate the withdrawal.
   * @param result The result object to update.
   */
  protected def calculateWithdrawal(month: Int, result: Result): Unit

  //  PRIVATE

  /**
   * Rounds a double value to two decimal places.
   *
   * @param x The value to be rounded.
   * @return The rounded value.
   */
  private def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  /**
   * Converts a percentage to a decimal value.
   *
   * @param percentage The percentage to convert.
   * @return The decimal representation of the percentage.
   */
  private def makeDecimalPercentage(percentage: Double): Double = {
    BigDecimal(percentage / 100).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  /**
   * Calculates the percentage for a given month and updates the result.
   *
   * @param month     The month for which to calculate the percentage.
   * @param result    The result object to update.
   * @param inflation The list of inflation rates.
   */
  private def calculatePercentage(month: Int, result: Result, inflation: List[Double]): Unit = {
    result.percentageArray(month) = 
      if (multiplier == 0 || (month % duration) < multiplierActivation) {
        makeDecimalPercentage(percentage)
      } else {
        val year: Int = month / 12
        makeDecimalPercentage(inflation.apply(year) + multiplier)
      }
  }

  /**
   * Calculates the penalty for a given month and updates the result.
   *
   * @param month  The month for which to calculate the penalty.
   * @param result The result object to update.
   */
  private def calculatePenalty(month: Int, result: Result): Unit = {
    result.penaltyArray(month) = 
      if ((month + 1) % duration == 0) {
        0
      } else {
        Math.min(result.quantityArray(month) * penalty, result.grossValueArray(month) - result.quantityArray(month) * price)
      }
  }

  /**
   * Calculates the final result for a given month and updates the result.
   *
   * @param month  The month for which to calculate the final result.
   * @param result The result object to update.
   */
  private def calculateFinalResult(month: Int, result: Result): Unit = {
    result.finalResultArray(month) = result.withdrawalArray(month) + result.accountArray(month)
  }

  /**
   * Calculates the quantity for a given month and updates the result.
   *
   * @param month  The month for which to calculate the quantity.
   * @param result The result object to update.
   */
  private def calculateQuantity(month: Int, result: Result): Unit = {
    result.quantityArray(month) = 
      if (month % duration == 0) {
        floor((result.withdrawalArray(month - 1) + result.accountArray(month - 1)) / change).toInt
      } else {
        result.quantityArray(month - 1)
      }
  }

  /**
   * Calculates the buy price for a given month and updates the result.
   *
   * @param month  The month for which to calculate the buy price.
   * @param result The result object to update.
   */
  private def calculateBuyPrice(month: Int, result: Result): Unit = {
    result.buyPriceArray(month) = if (month % duration == 0) change else result.buyPriceArray(month - 1)
  }

  /**
   * Initializes a result record for the bond.
   *
   * @param quantity  The initial quantity of the bond.
   * @param period    The total period for the bond.
   * @param inflation The list of inflation rates.
   * @return The initialized result record.
   */
  private def getInitialisedRecord(quantity: Int, period: Int, inflation: List[Double]): Result = {
    val result = new Result(period)

    result.quantityArray(0) = quantity
    result.buyPriceArray(0) = price
    result.basePriceArray(0) = quantity * price
    calculatePercentage(0, result, inflation)
    calculateGrossValue(0, result)
    calculatePenalty(0, result)
    calculateWithdrawal(0, result)
    calculateAccountStartingValue(result)
    calculateFinalResult(0, result)

    result.monthsArray.indices.foreach(i => result.monthsArray(i) = i + 1)

    result
  }
}
