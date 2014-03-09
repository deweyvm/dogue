package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, DogueRange}
import com.deweyvm.dogue.common.Implicits.Meters
import com.deweyvm.gleany.graphics.Color

case class AltitudinalRegion(name:String, code:Code, color:Color, range:DogueRange[Meters]) {
  override def toString = name
}

object Altitude {
  val SuperAlpine = AltitudinalRegion("Super Alpine", Code.▲,   Color.White.dim(1.1f), 4500.m <=> 10000.1.m)
  val Alpine      = AltitudinalRegion("Alpine",       Code.▲,   Color.White.dim(1.2f), 3600.m <=> 4500.m)
  val Subalpine   = AltitudinalRegion("Subalpine",    Code.▲,   Color.Grey,            2700.m <=> 3600.m)
  val Montane     = AltitudinalRegion("Montane",      Code.▲,   Color.DarkGreen,       900.m <=> 2700.m)
  val Highland    = AltitudinalRegion("Highland",     Code.^,   Color.Green,           500.m <=> 900.m)
  val Midlands    = AltitudinalRegion("Midlands",     Code.♠,   Color.LightGreen,      100.m <=> 500.m)
  val Lowlands    = AltitudinalRegion("Lowlands",     Code.`.`, Color.Yellow,          0.001.m <=> 100.m)
  val Oceanic     = AltitudinalRegion("Oceanic",      Code.~,   Color.Blue,            -100.m <=> 0.001.m)
  val Suboceanic  = AltitudinalRegion("Suboceanic",   Code.≈,   Color.DarkBlue,        -1000.m <=> -100.m)
  val Abyss       = AltitudinalRegion("Abyss",        Code.≈,   Color.DarkPurple,      -10000.m <=> -1000.m)
  val All = Vector(SuperAlpine, Alpine, Subalpine, Montane, Highland, Midlands, Lowlands, Oceanic, Suboceanic, Abyss)
  def fromHeight(m:Meters):AltitudinalRegion = All.find(_.range.contains(m)).getOrElse(Abyss)
}

