@main def main(): Unit = {
  val reader: JSONReader = new JSONReader()
  val bondListFromFile: BondList = reader.loadFromFile("data.json")
//  bondListFromFile.bonds.foreach(_.calculate(120))
//  println(bondListFromFile)
  bondListFromFile.bonds.find(_.name == "TOS").foreach(_.calculate(39))

}



