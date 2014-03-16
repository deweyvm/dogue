package com.deweyvm.dogue.loading

import com.deweyvm.dogue.world.{Latitude, LatitudinalRegion}

case class LatitudeRegionMap(map:Map[String, LatitudinalRegion]) {
  val regions = map.values.toVector
  def getLatitude(r:Double) = regions.find(_.range.contains(r)).getOrElse(Latitude.Void)
}
