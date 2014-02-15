package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip
import com.deweyvm.gleany.data.Point2d

object WorldTile {
  val Blank = WorldTile(0, 0, Color.Pink, Point2d.UnitX, Tile.Blank)
}

case class WorldTile(height:Double, danger:Double, region:Color, wind:Point2d, tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height %.5f" format height,
    "Danger %.5f" format danger,
    "Region %s" format region,
    "Wind %.2f" format wind.magnitude
  ))

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
