package com.deweyvm.dogue.world

import com.deweyvm.dogue.DogueImplicits.{Meters, Pressure}

case object AtmosphereConstants {
  val elrRate = 6.49/1000 //K m-1
  val waterPressurePerDepth = 0.1//atm m-1
  def airPressure(h:Meters):Pressure = {
    //pressure at sea level
    val p0 = 1//atm
    //temperature at sea level
    val T0 = 288.15//K
    //universal gas constant
    val R = 8.31447//J mol-1 K-1
    //acceleration due to gravity
    val g = 9.80665//m s-2
    //molar mass of dry air
    val M = 0.0289644//kg mol-1

    val num = g * M * h.d
    val denom = R * T0
    p0 * math.exp(-num/denom)
  }

  /**
   * sensical results require h to be negative
   * @param h height *above* sea level
   * @return pressure in atmospheres
   */
  def waterPressure(h:Meters):Pressure = {
    (-h.d*waterPressurePerDepth + 1).atm
  }
}
