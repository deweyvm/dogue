package com.deweyvm.whatever.world

import com.deweyvm.whatever.Assets
import com.deweyvm.gleany.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFontCache
import com.deweyvm.whatever.graphics.Renderer


object Tile {
  val font = Assets.font
}

class Tile(color:Color, symbol:String, x:Float, y:Float) {
  import Tile._
  val sprite = {
    val f = new BitmapFontCache(font)
    f.setColor(color.toLibgdxColor.toFloatBits)
    f.setText(symbol,0,0)
    f
  }

  def draw() {
    Renderer.draw(sprite)
  }
}

class Grid {
  val tiles:Array[Array[Tile]] = Array.tabulate(10,10) { case (i,j) =>
    new Tile(Color.White, "^", i*10, j*10)
  }

  def draw() {
    for (i <- 0 until 10; j <- 0 until 10) {
      tiles(i)(j).draw()
    }
  }
}
