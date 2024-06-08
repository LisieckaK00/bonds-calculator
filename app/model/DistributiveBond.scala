package model

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnoreProperties, JsonProperty}

import scala.math.floor

@JsonIgnoreProperties(ignoreUnknown = true)
case class DistributiveBond @JsonCreator() (
                                             @JsonProperty("name") name: String,
                                             @JsonProperty("percentage") percentage: Double,
                                             @JsonProperty("duration") duration: Double,
                                             @JsonProperty("distribution") distribution: Int,
                                             @JsonProperty("price") price: Int,
                                             @JsonProperty("change") change: Double,
                                             @JsonProperty("penalty") penalty: Double,
                                             @JsonProperty("multiplier") multiplier: Double,
                                             @JsonProperty("multiplierActivation") multiplierActivation: Int,
                                             @JsonProperty("description") description: String
                                           ) extends Bond {
  override protected def calculateWithdrawal(month: Int, result: Result): Unit = {
    result.withdrawalArray(month) =
      if ((month + 1) % distribution == 0) {
          result.basePriceArray(month)
      } else {
        (result.grossValueArray(month) - result.penaltyArray(month)) - (result.grossValueArray(month) - result.penaltyArray(month) - (result.basePriceArray(month))) * 0.19
      }
  }

  override protected def calculateAccount(month: Int, result: Result): Unit = {
    // if month == 0 throw exception
    result.accountArray(month) =
      if (month % duration == 0) {
        result.withdrawalArray(month - 1) + result.accountArray(month - 1) -
          floor((result.withdrawalArray(month - 1) + result.accountArray(month - 1)) / change) * change
      } else if ( (month + 1) % distribution == 0) {
        (result.grossValueArray(month) - (result.quantityArray(month) * price)) * 0.81 + result.accountArray(month - 1)
      } else {
        result.accountArray(month - 1)
      }
  }

  override protected def calculateAccountStartingValue(result: Result): Unit = {
    result.accountArray(0) =
      if ( 1 % distribution == 0) {
        (result.grossValueArray(0) - result.basePriceArray(0)) * 0.81
      } else {
        0
      }
  }

  override protected def calculateBasePrice(month: Int, result: Result): Unit = {
    result.basePriceArray(month) = result.quantityArray(month) * price
  }

  override protected def calculateGrossValue(month: Int, result: Result): Unit = {
    val tempMultiplier = if ((month + 1) % distribution != 0) then (month + 1) % distribution else distribution
    result.grossValueArray(month) = result.basePriceArray(month) * (1 + result.percentageArray(month) * tempMultiplier / 12)
  }

  override def getProperties: Map[String, Any] = {
    super.getProperties + ("type" -> "dist")
  }
}
