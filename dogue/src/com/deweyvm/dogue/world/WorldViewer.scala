package com.deweyvm.dogue.world

import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.gleany.data.Point2i
import com.deweyvm.dogue.Game

object WorldViewer {
  trait State
  case object WorldMap extends State
  case object RegionMap extends State

  def create(iView:Int, jView:Int, width:Int, height:Int):WorldViewer =
    new WorldViewer(iView, jView, width, height, WorldViewer.WorldMap)
}


class WorldViewer(iView:Int, jView:Int, width:Int, height:Int, state:WorldViewer.State) {
  def update(iMax:Int, jMax:Int): WorldViewer = {
    val iNew = (Controls.AxisX.zip(0, 1) + iView).clamp(0, iMax)
    val jNew = (Controls.AxisY.zip(0, 1) + jView).clamp(0, jMax)
    val s =
      if (Controls.Enter.justPressed) {
        WorldViewer.RegionMap
      } else if (Controls.Backspace.justPressed) {
        WorldViewer.WorldMap
      } else {
        state
      }
    new WorldViewer(iNew, jNew, width, height, s)
  }

  def getCursor:(Int,Int) = (iView + xOffset, jView + yOffset)

  def getTooltip(grid:World):Option[(Color, Vector[String])] = {
    val f = (grid.tiles.get _).tupled
    f(getCursor) map {t => (Color.Red, Vector("Height %.5f" format t.height))}
  }

  private val xOffset = width/2
  private val yOffset = height/2 - 1

  def draw(grid:World, iRoot:Int, jRoot:Int) {

    grid.tiles slice (iView, jView, width, height) foreach { case (i, j, tile) =>
      val x = iRoot + i
      val y = jRoot + j
      tile foreach { _.tile.draw(x, y)}
    }
    state match {
      case WorldViewer.WorldMap =>
        if (Game.getFrame % 120 < 60) {
          new Tile(Code.+, Color.Black, Color.White).draw(iRoot + xOffset, jRoot + yOffset)
        }
      case WorldViewer.RegionMap =>

        val f = (grid.regions.get _).tupled
        f(getCursor) foreach { r =>
          val (ox, oy) = ((width - r.width)/2, (height - r.height)/2)
          r.tiles foreach { case (i, j, t) =>
            t.draw(i + iRoot + ox, j + jRoot + oy)
          }

        }
    }

  }
}
