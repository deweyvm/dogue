package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._

object WorldTile {
  val Blank = WorldTile(0 m, Color.Pink, Polar, Point2d.UnitX, 0, 0,  Summer, Tile.Blank)
}

case class WorldTile(height:Meters, region:Color, latitude:LatitudinalRegion, wind:Point2d, daylight:Double, sunTemp:Double, season:Season, tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height %.2f" format height.d,
    "Region %d" format region.toLibgdxColor.toIntBits,
    "Latitude %s" format latitude,
    "Wind %.2f" format wind.magnitude,
    "Daylight %.2f" format daylight,
    "Sun Temp %.2f" format sunTemp,
    pressureString,
    "",
    "Season: %s" format season.toString
  ))

  private def pressureString = {
    import AtmosphereConstants._
    val f = (height < 0) select (waterPressure _, airPressure _)
    "Pressure %.2f" format f(height)
  }

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
