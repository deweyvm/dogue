package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.Implicits.Meters

case class AltitudinalRegion(range:DogueRange[Meters])
object Altitude {
  val SuperAlpine = AltitudinalRegion(4500.m <=> 10000.m)
  val Alpine      = AltitudinalRegion(3600.m <=> 4500.m)
  val Subalpine   = AltitudinalRegion(2700.m <=> 3600.m)
  val Montane     = AltitudinalRegion(900.m <=> 2700.m)
  val Highland    = AltitudinalRegion(500.m <=> 900.m)
  val Midlands    = AltitudinalRegion(100.m <=> 500.m)
  val Lowlands    = AltitudinalRegion(-10.m <=> 100.m)
  val Oceanic     = AltitudinalRegion(-100.m <=> -10.m)
  val Suboceanic  = AltitudinalRegion(-1000.m <=> -100.m)
  val Abyss       = AltitudinalRegion(-10000.m <=> -1000.m)
  val All = Vector(SuperAlpine, Alpine, Subalpine, Montane, Highland, Midlands, Lowlands, Oceanic, Suboceanic, Abyss)
  def fromHeight(m:Meters):Option[AltitudinalRegion] = All.find(_.range.contains(m))
}

