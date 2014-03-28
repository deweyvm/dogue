package com.deweyvm.dogue.loading

import com.deweyvm.dogue.world.{Altitude, AltitudinalRegion}
import com.deweyvm.dogue.DogueImplicits.Meters

case class AltitudeRegionMap(map:Map[String, AltitudinalRegion]) {
  val regions = map.values.toVector
  def fromHeight(m:Meters):AltitudinalRegion = regions.find(_.range.contains(m)).getOrElse(Altitude.Void)
}
