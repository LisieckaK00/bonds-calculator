package model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.io.File

class JSONReader {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  val module = new SimpleModule()
  module.addDeserializer(classOf[Bond], new BondDeserializer(classOf[Bond]))
  mapper.registerModule(module)

  def loadFromFile(fileName: String): BondList = {
    val jsonFile = new File(fileName)
    mapper.readValue(jsonFile, classOf[BondList])
  }

}
