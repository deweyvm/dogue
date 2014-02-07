package com.deweyvm.dogue.graphics

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.Dogue

object StageRenderer {
  def create(cols:Int, rows:Int):StageRenderer = {
    new StageRenderer(cols, rows, Array2d.fill(cols, rows)(None : Option[Tile]))
  }
}

class StageRenderer(cols:Int, rows:Int, val draws:Array2d[Option[Tile]]) {
  def draw(i:Int, j:Int, tile:Tile):StageRenderer = {
    new StageRenderer(cols, rows, draws.put(i, j, Some(tile)))
  }

  def render() {
    draws foreach { case (i, j, tile) =>
      tile foreach {_.draw(i, j)}
    }
  }

}
