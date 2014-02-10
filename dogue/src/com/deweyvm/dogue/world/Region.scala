package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.{Code, Array2d}
import com.deweyvm.gleany.graphics.Color

class Region(val width:Int, val height:Int) {
  val tiles = Array2d.tabulate[Tile](width, height) { case(i,j) =>
    new Tile(Code.random, Color.White, Color.Pink)
  }

  def draw() {
    tiles foreach { case (i, j, t) =>
      t.draw(i, j)
    }
  }
}
