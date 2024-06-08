package model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.io.File

/**
 * Utility class for reading JSON data and converting it into objects.
 */
class JSONReader {
  // Initialize ObjectMapper with Scala module for Scala class support
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  // Register custom deserializer for Bond objects
  private val module = new SimpleModule()
  module.addDeserializer(classOf[Bond], new BondDeserializer(classOf[Bond]))
  mapper.registerModule(module)

  /**
   * Loads JSON data from a file and converts it into a BondList object.
   *
   * @param fileName The name of the JSON file to load.
   * @return A BondList object representing the deserialized JSON data.
   */
  def loadFromFile(fileName: String): BondList = {
    val jsonFile = new File(fileName)
    mapper.readValue(jsonFile, classOf[BondList])
  }

}
