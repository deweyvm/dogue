package com.deweyvm.whatever.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.Tile
import com.deweyvm.whatever.input.Controls
import com.deweyvm.gleany.GleanyMath


class Grid(width:Int, height:Int, cols:Int, rows:Int, glyphs:GlyphFactory) {
  var iOffset = 0
  var jOffset = 0
  val tiles:Array[Array[Tile]] = Array.tabulate(cols, rows) { case (i,j) =>
    new Tile(Color.Orange, Color.White, (Math.random()*256).toInt, glyphs)
  }

  def update() {
    if (Controls.Up.justPressed) {
      jOffset -= 1
    } else if (Controls.Down.justPressed) {
      jOffset += 1
    }

    if (Controls.Left.justPressed) {
      iOffset -= 1
    } else if (Controls.Right.justPressed) {
      iOffset += 1
    }

    iOffset = GleanyMath.clamp(iOffset, 0, cols - width - 1)
    jOffset = GleanyMath.clamp(jOffset, 0, rows - height - 1)
  }

  def draw(iRoot:Int, jRoot:Int) {
    //val imin = scala.math.max(iOffset, cols - width)
    //val imax = scala.math.min()
    for (i <- 0 until width; j <- 0 until height) {
      val iTile = i + iOffset
      val jTile = j + jOffset
      val x = iRoot + i
      val y = jRoot + j
      if (iTile >= 0 && jTile >= 0 && iTile < cols && jTile < rows) {
        tiles(iTile)(jTile).draw(x, y)
      }
    }
  }
}
