package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.data.Array2d


class Grid(width:Int, height:Int, val cols:Int, val rows:Int, glyphs:GlyphFactory) {
  val (iSpawn, jSpawn) = (0,0)

  val tiles:Array2d[Tile] = Array2d.tabulate(cols, rows) { case (i,j) =>
    new Tile(Color.Orange, Color.White, (Math.random()*256).toInt, glyphs)
  }

  def update:Grid = this

}


