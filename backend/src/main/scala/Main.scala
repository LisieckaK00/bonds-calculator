@main def main(): Unit = {
  val reader: JSONReader = new JSONReader()
  val bondListFromFile: BondList = reader.loadFromFile("data.json")
//  bondListFromFile.bonds.foreach(_.calculate(144))
//  println(bondListFromFile)
  bondListFromFile.bonds.find(_.name == "COI").foreach(_.calculate(38))

}



