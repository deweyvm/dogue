package com.deweyvm.whatever.world

import com.deweyvm.gleany.GleanyMath
import com.deweyvm.whatever.input.Controls

class GridView(iView:Int, jView:Int, width:Int, height:Int) {
  def update(iMax:Int, jMax:Int): GridView = {
    val iNew = GleanyMath.clamp(Controls.AxisX.justPressed + iView, 0, iMax)
    val jNew = GleanyMath.clamp(Controls.AxisY.justPressed + jView, 0, jMax)
    new GridView(iNew, jNew, width, height)
  }

  def draw(grid:Grid, iRoot:Int, jRoot:Int) {
    grid.tiles slice (iView, jView, width, height) foreach { case (i, j, tile) =>
      val x = iRoot + i
      val y = jRoot + j
      tile foreach {_.draw(x, y)}
    }
  }
}
