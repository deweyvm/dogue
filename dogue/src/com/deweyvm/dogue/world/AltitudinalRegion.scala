package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits.Meters

case class AltitudinalRegion(name:String, range:DogueRange[Meters]) {
  override def toString = name
}

object Altitude {
  val All = Vector(
    AltitudinalRegion("Super Alpine",  4500.m <=> 10000.1.m),
    AltitudinalRegion("Alpine",        3600.m <=> 4500.m),
    AltitudinalRegion("Subalpine",     2700.m <=> 3600.m),
    AltitudinalRegion("Montane",       900.m <=> 2700.m),
    AltitudinalRegion("Highlands",     500.m <=> 900.m),
    AltitudinalRegion("Midlands",      50.m <=> 500.m),
    AltitudinalRegion("Lowlands",      0.m <=> 50.m),
    AltitudinalRegion("Oceanic",      -100.m <=> 0.m),
    AltitudinalRegion("Suboceanic",   -1000.m <=> -100.m),
    AltitudinalRegion("Abyss",        -10000.m <=> -1000.m)
  )

  def fromHeight(m:Meters):AltitudinalRegion = All.find(_.range.contains(m)).getOrElse(Abyss)
}

