package com.deweyvm.whatever.ui

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.world.{Grid, GridView}

object WorldPanel {
  def create(iSpawn:Int, jSpawn:Int, x:Int, y:Int, width:Int, height:Int, cols:Int, rows:Int, factory:GlyphFactory):WorldPanel = {
    val view = new GridView(iSpawn, jSpawn, width, height)
    val grid = new Grid(width, height, cols, rows, factory)
    new WorldPanel(x, y, width, height, grid, view)
  }
}


class WorldPanel(x:Int, y:Int, width:Int, height:Int, grid:Grid, view:GridView)
  extends Panel(x, y, width, height) {
  val (iSpawn, jSpawn) = (0,0)
  override def update():WorldPanel = {
    val newView = view.update(grid.cols - width - 1, grid.rows - height - 1)
    val newGrid = grid.update
    new WorldPanel(x, y, width, height, newGrid, newView)
  }

  override def draw() {
    view.draw(grid, x, y)
  }
}
