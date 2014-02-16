package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, Indexed2d}
import com.deweyvm.gleany.input.Control
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.Implicits
import Implicits._

case class ArrayViewer(viewWidth:Int, viewHeight:Int, xCursor:Int, yCursor:Int, xControl:Control[Int], yControl:Control[Int]) {
  private val crosshair = new Tile(Code.+, Color.Red, Color.Pink)

  def update[T](a:Indexed2d[T], scale:Int):ArrayViewer = {
    val width = a.cols
    val height = a.rows
    val result = this.copy(xCursor = (xCursor + xControl.zip(5,1)*scale).clamp(0, width - 1),
              yCursor = (yCursor + yControl.zip(5,1)*scale).clamp(0, height - 1))
    //println(result.xCursor)
    result
  }

  def withCursor(x:Int, y:Int) = this.copy(xCursor = x, yCursor = y)

  def scaled(div:Int) = this.copy(xCursor = xCursor/div, yCursor = yCursor/div)

  def draw[T](a:Indexed2d[T], iRoot:Int, jRoot:Int, draw:(Int,Int,T) => Unit, default: => T) {
    val width = a.cols
    val height = a.rows
    val iView = (xCursor - viewHeight/2).clamp(0, width - viewWidth)
    val jView = (yCursor - viewHeight/2).clamp(0, height - viewHeight)
    a.slice(iView, jView, viewWidth, viewHeight, x => x, default) foreach { case (i, j, tile) =>
      val x = iRoot + i
      val y = jRoot + j
      draw(x, y, tile)
    }
    if (Game.getFrame % 120 < 100) {
      val xCrosshair = (iRoot + xCursor - iView).clamp(iRoot, iRoot + viewWidth - 1)
      val yCrosshair = (jRoot + yCursor - jView).clamp(jRoot, jRoot + viewHeight - 1)
      crosshair.draw(xCrosshair, yCrosshair)
    }
  }
}
