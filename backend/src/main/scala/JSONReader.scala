import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.File

class JSONReader {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def loadFromFile(fileName: String): BondList = {
    val jsonFile = new File(fileName)
    mapper.readValue(jsonFile, classOf[BondList])
  }

}
