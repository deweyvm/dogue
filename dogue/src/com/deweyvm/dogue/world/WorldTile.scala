package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip

case class WorldTile(height:Double, danger:Double, region:Color, tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height %.5f" format height,
    "Danger %.5f" format danger,
    "Region %s" format region

  ))

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
