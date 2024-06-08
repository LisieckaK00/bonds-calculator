package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represent a list of bonds.
 *
 * @param bonds List of bonds.
 */
case class BondList(@JsonProperty("bonds") bonds: List[Bond])