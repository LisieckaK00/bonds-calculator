package model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnoreProperties, JsonProperty}

import scala.math.floor

@JsonIgnoreProperties(ignoreUnknown = true)
case class AccumulativeBond @JsonCreator() (
                                             @JsonProperty("name") name: String,
                                             @JsonProperty("percentage") percentage: Double,
                                             @JsonProperty("duration") duration: Double,
                                             @JsonProperty("capitalization") capitalization: Int,
                                             @JsonProperty("price") price: Int,
                                             @JsonProperty("change") change: Double,
                                             @JsonProperty("penalty") penalty: Double,
                                             @JsonProperty("multiplier") multiplier: Double,
                                             @JsonProperty("multiplierActivation") multiplierActivation: Int,
                                             @JsonProperty("description") description: String
                                           ) extends Bond {
  override protected def calculateWithdrawal(month: Int, result: Result): Unit = {
    result.withdrawalArray(month) = Math.max(result.quantityArray(month) * result.buyPriceArray(month) +
      (result.grossValueArray(month) - result.penaltyArray(month) - result.quantityArray(month) * result.buyPriceArray(month)) * 0.81,
      result.quantityArray(month) * price)
  }

  override protected def calculateAccount(month: Int, result: Result): Unit = {
    val tempVal = result.quantityArray(month - 1) * price + (result.withdrawalArray(month - 1) -
      result.quantityArray(month - 1) * price) + result.accountArray(month - 1)

    result.accountArray(month) = 
      if (month % duration == 0) {
        tempVal - (floor(tempVal / change).toInt * change)
      } else {
        result.accountArray(month - 1)
      }
  }

  override protected def calculateBasePrice(month: Int, result: Result): Unit = {
    result.basePriceArray(month) = 
      if (month % duration == 0) {
        result.quantityArray(month) * price
      } else if (month % capitalization == 0) {
        result.grossValueArray(month - 1)
      } else {
        result.basePriceArray(month - 1)
      }
  }
  
  override protected def calculateGrossValue(month: Int, result: Result): Unit = {
    val currentMultiplier = if ((month + 1) % capitalization != 0) (month + 1) % capitalization else capitalization
    result.grossValueArray(month) = result.basePriceArray(month) * (1 + (result.percentageArray(month) * currentMultiplier) / 12)
  }

  override def getProperties: Map[String, Any] = {
    super.getProperties + ("type" -> "acc")
  }
}
