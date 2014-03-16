package com.deweyvm.dogue.loading

import com.deweyvm.dogue.world.AltitudinalRegion
import com.deweyvm.dogue.common.CommonImplicits.Meters

case class AltitudeRegionMap(map:Map[String, AltitudinalRegion]) {
  val regions = map.values.toVector
  //fixme, possible oob error for invalid map
  def fromHeight(m:Meters):AltitudinalRegion = regions.find(_.range.contains(m)).getOrElse(regions(0))
}
