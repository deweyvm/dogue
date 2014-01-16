package com.deweyvm.whatever.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.Tile
import com.deweyvm.whatever.data.Array2d


class Grid(width:Int, height:Int, val cols:Int, val rows:Int, glyphs:GlyphFactory) {
  val (iSpawn, jSpawn) = (0,0)

  val tiles:Array2d[Tile] = Array2d.tabulate(cols, rows) { case (i,j) =>
    new Tile(Color.Orange, Color.White, (Math.random()*256).toInt, glyphs)
  }

  def update:Grid = this

}


