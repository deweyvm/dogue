package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Array2dView, Code}
import com.deweyvm.gleany.input.Control
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

case class ArrayViewer(viewWidth:Int, viewHeight:Int, xCursor:Int, yCursor:Int, xControl:Control[Int], yControl:Control[Int]) {
  private val crosshair = new Tile(Code.+, Color.Red, Color.Pink)

  def update[T](a:Array2dView[T], scale:Int):ArrayViewer = {
    val width = a.cols
    val height = a.rows
    this.copy(xCursor = (xCursor + xControl.zip(5,1)*scale).clamp(0, width - 1),
              yCursor = (yCursor + yControl.zip(5,1)*scale).clamp(0, height - 1))
  }

  def scaled(div:Int) = this.copy(xCursor = xCursor/div, yCursor = yCursor/div)

  def draw[T](a:Array2dView[T], iRoot:Int, jRoot:Int, draw:(T,Int,Int) => Unit) {
    val width = a.cols
    val height = a.rows
    val iView = (xCursor - viewHeight/2).clamp(0, width - viewWidth)
    val jView = (yCursor - viewHeight/2).clamp(0, height - viewHeight)
    a.slice(iView, jView, viewWidth, viewHeight) foreach { case (i, j, tile) =>
      val x = iRoot + i
      val y = jRoot + j
      draw(tile, x, y)
    }
    if (Game.getFrame % 120 < 100) {
      //prevents scrolling off the edge for non-evenly dividing scales
      val xCrosshair = (iRoot + xCursor - iView).clamp(iRoot, iRoot + viewWidth - 1)
      val yCrosshair = (jRoot + yCursor - jView).clamp(jRoot, jRoot + viewHeight - 1)
      crosshair.draw(xCrosshair, yCrosshair)
    }
  }
}
