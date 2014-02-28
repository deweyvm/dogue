package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._

object WorldTile {
  val Blank = WorldTile(0 m, 1 atm, 0, Color.Pink, Polar, Point2d.UnitX, 0, 0,  Summer, Tile.Blank)
}

case class WorldTile(height:Meters,
                     pressure:Pressure,
                     regionIndex:Int,
                     regionColor:Color,
                     latitude:LatitudinalRegion,
                     wind:Point2d,
                     daylight:Double,
                     sunTemp:Double,
                     season:Season,
                     tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height %.2f" format height.d,
    "Region %d" format regionIndex,
    "Latitude %s" format latitude,
    "Wind %.2f" format wind.magnitude,
    "Daylight %.2f" format daylight,
    "Sun Temp %.2f" format sunTemp,
    "Pressure %.2f" format pressure.d,
    "",
    "Season: %s" format season.toString
  ))

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
