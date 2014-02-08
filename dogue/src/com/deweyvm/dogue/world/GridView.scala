package com.deweyvm.dogue.world

import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.Dogue

class GridView(iView:Int, jView:Int, width:Int, height:Int) {
  def update(iMax:Int, jMax:Int): GridView = {
    val iNew = (Controls.AxisX.zip(0, 15) + iView).clamp(0, iMax)
    val jNew = (Controls.AxisY.zip(0, 15) + jView).clamp(0, jMax)
    new GridView(iNew, jNew, width, height)
  }

  def draw(grid:Grid, iRoot:Int, jRoot:Int) {
    grid.tiles slice (iView, jView, width, height) foreach { case (i, j, tile) =>
      val x = iRoot + i
      val y = jRoot + j
      tile foreach { _.draw(x, y)}
    }
  }
}
