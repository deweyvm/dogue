package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip
import com.deweyvm.gleany.data.Point2d

object WorldTile {
  val Blank = WorldTile(0, Color.Pink, Polar, Point2d.UnitX, Tile.Blank)
}

case class WorldTile(height:Double, region:Color, latitude:LatitudinalRegion, wind:Point2d, tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height %.2f" format height,
    "Region %d" format region.toLibgdxColor.toIntBits,
    "Latitude %s" format latitude,
    "Wind %.2f" format wind.magnitude
  ))

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
