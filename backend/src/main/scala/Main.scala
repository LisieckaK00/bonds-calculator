@main def main(): Unit = {
  val reader: JSONReader = new JSONReader()
  val bondListFromFile: BondList = reader.loadFromFile("data.json")
//  bondListFromFile.bonds.foreach(_.calculate(36))
//  println(bondListFromFile)
  bondListFromFile.bonds.find(_.name == "OTS").foreach(_.calculate(12))

}



