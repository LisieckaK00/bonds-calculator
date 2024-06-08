package model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode}

/**
 * Custom deserializer for deserializing JSON into Bond objects.
 *
 * This deserializer handles the deserialization of JSON data into instances of subclasses of the Bond trait,
 * based on the "type" property in the JSON.
 *
 * @param vc The class of the value to deserialize.
 *
 */
class BondDeserializer(vc: Class[?]) extends StdDeserializer[Bond](vc) {

  /**
   * Deserializes JSON into a Bond object.
   *
   * @param p    The JSON parser.
   * @param ctxt The deserialization context.
   * @return A Bond object representing the deserialized JSON data.
   * @throws IllegalArgumentException if the "type" property in the JSON is unknown.
   */
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Bond = {
    val node: JsonNode = p.getCodec.readTree(p)
    val bondType: String = node.get("type").asText()

    bondType match {
      case "acc" => p.getCodec.treeToValue(node, classOf[AccumulativeBond])
      case "dist" => p.getCodec.treeToValue(node, classOf[DistributiveBond])
      case _     => throw new IllegalArgumentException(s"Unknown bond_type: $bondType")
    }
  }
}
