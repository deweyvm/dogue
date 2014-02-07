package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.{Code, Array2d}


class Grid(width:Int, height:Int, val cols:Int, val rows:Int) {
  val (iSpawn, jSpawn) = (0,0)

  val tiles:Array2d[Tile] = Array2d.tabulate(cols, rows) { case (i,j) =>
    new Tile(Code.random, Color.Orange, Color.White)
  }

  def update:Grid = this

}


