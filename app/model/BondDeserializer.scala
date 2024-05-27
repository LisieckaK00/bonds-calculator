package model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonNode}

class BondDeserializer(vc: Class[?]) extends StdDeserializer[Bond](vc) {

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
