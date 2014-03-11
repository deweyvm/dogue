package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, DogueRange}
import com.deweyvm.dogue.common.Implicits.Meters
import com.deweyvm.gleany.graphics.Color

case class AltitudinalRegion(name:String, range:DogueRange[Meters]) {
  override def toString = name
}

object Altitude {
  val SuperAlpine = AltitudinalRegion("Super Alpine", 4500.m <=> 10000.1.m)
  val Alpine      = AltitudinalRegion("Alpine",       3600.m <=> 4500.m)
  val Subalpine   = AltitudinalRegion("Subalpine",    2700.m <=> 3600.m)
  val Montane     = AltitudinalRegion("Montane",      900.m <=> 2700.m)
  val Highlands   = AltitudinalRegion("Highlands",    500.m <=> 900.m)
  val Midlands    = AltitudinalRegion("Midlands",     100.m <=> 500.m)
  val Lowlands    = AltitudinalRegion("Lowlands",     0.001.m <=> 100.m)
  val Oceanic     = AltitudinalRegion("Oceanic",      -100.m <=> 0.001.m)
  val Suboceanic  = AltitudinalRegion("Suboceanic",   -1000.m <=> -100.m)
  val Abyss       = AltitudinalRegion("Abyss",        -10000.m <=> -1000.m)
  val All = Vector(SuperAlpine, Alpine, Subalpine, Montane, Highlands, Midlands, Lowlands, Oceanic, Suboceanic, Abyss)
  def fromHeight(m:Meters):AltitudinalRegion = All.find(_.range.contains(m)).getOrElse(Abyss)
}

