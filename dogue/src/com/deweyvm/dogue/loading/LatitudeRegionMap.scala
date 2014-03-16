package com.deweyvm.dogue.loading

import com.deweyvm.dogue.world.LatitudinalRegion

case class LatitudeRegionMap(map:Map[String, LatitudinalRegion]) {
  val regions = map.values.toVector
  //fixme, possible oob error for invalid map
  def getLatitude(r:Double) = regions.find(_.range.contains(r)).getOrElse(regions(0))
}
