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
    result.withdrawalArray(month) = 0
  }

  override protected def calculateAccount(month: Int, result: Result): Unit = {
    result.accountArray(month) = 0
  }

  override protected def calculateBasePrice(month: Int, result: Result): Unit = {
    result.basePriceArray(month) = 0
  }

  override protected def calculateGrossValue(month: Int, result: Result): Unit = {
    result.grossValueArray(month) = 0
  }

  override def getProperties: Map[String, Any] = {
    super.getProperties + ("type" -> "dist")
  }
}
