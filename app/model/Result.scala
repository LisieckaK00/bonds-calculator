package model

class Result(val period: Int) {
  var monthsArray: Array[Int] = Array.fill[Int](period)(-1)
  var quantityArray: Array[Int] = Array.fill[Int](period)(-1)
  var buyPriceArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var basePriceArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var percentageArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var grossValueArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var penaltyArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var withdrawalArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var accountArray: Array[Double] = Array.fill[Double](period)(-1.0)
  var finalResultArray: Array[Double] = Array.fill[Double](period)(-1.0)

  def mergeArrays(): Array[Array[Double]] = {
    Array.tabulate(period) { month =>
      Array(
        monthsArray(month).toDouble,
        quantityArray(month).toDouble,
        buyPriceArray(month),
        basePriceArray(month),
        percentageArray(month),
        grossValueArray(month),
        penaltyArray(month),
        withdrawalArray(month),
        accountArray(month),
        finalResultArray(month)
      )
    }
  }
}
