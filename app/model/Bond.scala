package model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
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
                                 @JsonProperty("description") description: String
                               ) {

  val inflation: Double = 5

  private def roundToTwoPlaces(x: Double): Double = {
    BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def makeDecimalPercentage(percentage: Double): Double = {
    BigDecimal(percentage / 100).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def calculatePercentage(month: Int, result: Result): Unit = {
    if (result.percentageArray(month) == -1.0) {
      result.percentageArray(month) = if (multiplier == 0 || month + 1 <= multiplierActivation) {
        makeDecimalPercentage(percentage)
      } else {
        makeDecimalPercentage(inflation + multiplier)
      }
    }
  }

  private def calculateGrossValue(month: Int, result: Result): Unit = {
    if (result.grossValueArray(month) == -1.0) {
      val currentMultiplier = if ((month + 1) % capitalization != 0) (month + 1) % capitalization else capitalization
      result.grossValueArray(month) = result.basePriceArray(month) * (1 + (result.percentageArray(month) * currentMultiplier) / 12)
    }
  }

  private def calculatePenalty(month: Int, result: Result): Unit = {
    if (result.penaltyArray(month) == -1.0) {
      result.penaltyArray(month) = if ((month + 1) % duration == 0) {
        0
      } else {
        Math.min(result.quantityArray(month) * penalty, result.grossValueArray(month) - result.quantityArray(month) * price)
      }
    }
  }

  private def calculateWithdrawal(month: Int, result: Result): Unit = {
    if (result.withdrawalArray(month) == -1.0) {
      result.withdrawalArray(month) = Math.max(result.quantityArray(month) * result.buyPriceArray(month) +
        (result.grossValueArray(month) - result.penaltyArray(month) - result.quantityArray(month) * result.buyPriceArray(month)) * 0.81,
        result.quantityArray(month) * price)
    }
  }

  private def calculateAccount(month: Int, result: Result): Unit = {
    if (result.accountArray(month) == -1.0) {
      val tempVal = result.quantityArray(month - 1) * price + (result.withdrawalArray(month - 1) -
        result.quantityArray(month - 1) * price) + result.accountArray(month - 1)

      result.accountArray(month) = if (month % duration == 0) {
        tempVal - (floor(tempVal / change).toInt * change)
      } else {
        result.accountArray(month - 1)
      }
    }
  }

  private def calculateFinalResult(month: Int, result: Result): Unit = {
    if (result.finalResultArray(month) == -1.0) {
      result.finalResultArray(month) = result.withdrawalArray(month) + result.accountArray(month)
    }
  }

  private def calculateQuantity(month: Int, result: Result): Unit = {
    if (result.quantityArray(month) == -1) {
      result.quantityArray(month) = if (month % duration == 0) {
        floor((result.withdrawalArray(month - 1) + result.accountArray(month - 1)) / change).toInt
      } else {
        result.quantityArray(month - 1)
      }
    }
  }

  private def calculateBuyPrice(month: Int, result: Result): Unit = {
    if (result.buyPriceArray(month) == -1.0) {
      result.buyPriceArray(month) = if (month % duration == 0) change else result.buyPriceArray(month - 1)
    }
  }

  private def calculateBasePrice(month: Int, result: Result): Unit = {
    if (result.basePriceArray(month) == -1.0) {
      result.basePriceArray(month) = if (month % duration == 0) {
        result.quantityArray(month) * price
      } else if (month % capitalization == 0) {
        result.grossValueArray(month - 1)
      } else {
        result.basePriceArray(month - 1)
      }
    }
  }

  def getProperties: Map[String, Any] = {
    Map(
      "name" -> name,
      "percentage" -> percentage,
      "multiplier" -> multiplier,
      "multiplierActivation" -> multiplierActivation,
      "type" -> bond_type,
      "description" -> description
    )
  }

  def calculateEndValue(quantity: Int, period: Int): Array[Array[Double]] = {
    val result = new Result(period)

    result.quantityArray(0) = quantity
    result.buyPriceArray(0) = price
    result.basePriceArray(0) = quantity * price
    result.accountArray(0) = 0

    result.monthsArray.indices.foreach(i => result.monthsArray(i) = i + 1)

    for (month <- 0 until period) {
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

    println(f"Month | Quantity | BuyPrice | BasePrice | Percentage | GrossValue | Penalty | Withdrawal | Account | FinalResult")
    for (month <- 0 until period) {
      println(f"${result.monthsArray(month)}%5d | ${result.quantityArray(month)}%8d | ${result.buyPriceArray(month)}%8.2f | ${result.basePriceArray(month)}%9.2f | ${result.percentageArray(month)}%10.4f | ${result.grossValueArray(month)}%10.2f | ${result.penaltyArray(month)}%7.2f | ${result.withdrawalArray(month)}%10.2f | ${result.accountArray(month)}%7.2f | ${result.finalResultArray(month)}%11.2f")
    }

    result.mergeArrays()
  }
}
