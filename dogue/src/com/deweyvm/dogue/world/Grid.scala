package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.{Code, Array2d}
import com.deweyvm.dogue.common.procgen.PerlinNoise


class Grid(width:Int, height:Int, val cols:Int, val rows:Int) {
  val (iSpawn, jSpawn) = (0,0)

  val tiles:Array2d[Tile] = {
    val noise = PerlinNoise.default.render
    noise.cut[Double](cols, rows, x => x, 0).map({ case (i,j, t) =>
      new Tile(Code.intToCode((t*13 + 13).toInt), Color.Orange, Color.White)
    })
    /*Array2d.tabulate(cols, rows) { case (i,j, t) =>
      new Tile(Code.intToCode((noise(i)(j)*13 + 13).toInt), Color.Orange, Color.White)
    }*/
  }

  def update:Grid = this

}


