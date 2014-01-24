package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.dogue.world.{Grid, GridView}
import com.deweyvm.gleany.graphics.Color

object WorldPanel {
  def create(iSpawn:Int, jSpawn:Int, x:Int, y:Int, width:Int, height:Int, bgColor:Color, cols:Int, rows:Int, factory:GlyphFactory):WorldPanel = {
    val view = new GridView(iSpawn, jSpawn, width, height)
    val grid = new Grid(width, height, cols, rows, factory)
    new WorldPanel(x, y, width, height, bgColor, grid, view)
  }
}


case class WorldPanel(override val x:Int,
                      override val y:Int,
                      override val width:Int,
                      override val height:Int,
                      bgColor:Color,
                      grid:Grid,
                      view:GridView)
  extends Panel(x, y, width, height, bgColor) {
  val (iSpawn, jSpawn) = (0,0)
  override def update():WorldPanel = {
    val newView = view.update(grid.cols - width - 1, grid.rows - height - 1)
    val newGrid = grid.update
    this.copy(grid = newGrid, view = newView)
  }

  override def draw() {
    super.draw()
    view.draw(grid, x, y)
  }
}
