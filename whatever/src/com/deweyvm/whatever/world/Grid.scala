package com.deweyvm.whatever.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.Tile
import com.deweyvm.whatever.input.Controls


class Grid(cols:Int, rows:Int, glyphs:GlyphFactory) {
  var iOffset = 0
  var jOffset = 0
  val tiles:Array[Array[Tile]] = Array.tabulate(cols, rows) { case (i,j) =>
    new Tile(Color.Orange, Color.White, (Math.random()*256).toInt, glyphs)
  }

  def update() {
    if (Controls.Up.justPressed) {
      iOffset += 1
    } else if (Controls.Down.justPressed) {
      iOffset -= 1
    }
  }

  def draw(width:Int, height:Int, iRoot:Int, jRoot:Int) {
    //val imin = scala.math.max(iOffset, cols - width)
    //val imax = scala.math.min()
    for (i <- 0 until cols; j <- 0 until rows) {
      val ii = iOffset + i
      val jj = j
      if (ii > 0 && jj > 0 && ii < cols && jj < rows) {
        tiles(i)(j).draw(iRoot + ii, jRoot + jj)
      }
    }
  }
}
