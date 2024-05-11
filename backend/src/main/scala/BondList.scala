import com.fasterxml.jackson.annotation.JsonProperty

case class BondList(@JsonProperty("bonds") bonds: List[Bond])