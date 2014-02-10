package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color

case class WorldTile(height:Double, tile:Tile) {
  def tooltip = (Color.Red, Vector("Height %.5f" format height))
}
