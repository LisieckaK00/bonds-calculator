@main def main(): Unit = {
  val reader: JSONReader = new JSONReader()
  val bondListFromFile: BondList = reader.loadFromFile("data.json")
  bondListFromFile.bonds.foreach(_.calculate(12))
//  println(bondListFromFile)
//  bondListFromFile.bonds.find(_.name == "ROR").foreach(_.calculate(13))

}



