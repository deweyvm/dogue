package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.Dogue

/*
 * have continuous scrollbar
 * iterate over each discrete half square.
 * if the scroll bar is at the start, fill in the topmost half-square
 * if the scroll bar is at the end, fill in the bottommost half-square
 * for each remaining half square: if it collides with the continuous scrollbar, add it to the queue, otherwise continue
 * when done, merge all overlapping half squares to full squares
 */
class ScrollBar {
  private def makeTile(code:Code):Tile =
    new Tile(code, Color.Black, Color.White)


  def drawTile(t:Tile)(i:Int, j:Int) {
    Dogue.renderer.draw(t, i, j)
  }

  val upArrow = makeTile(Code.▲)
  val downArrow = makeTile(Code.▼)
  val lineTile = makeTile(Code.─)

  def draw(numLines:Int, j:Int, width:Int, height:Int, iRoot:Int, jRoot:Int) {
    import scala.math._
    if (numLines > height) {
      drawTile(upArrow)(iRoot + width - 1, jRoot)
      drawTile(downArrow)(iRoot + width - 1, jRoot + height - 1)
      val percentProgress = j.toFloat/(numLines - height - 2)
      val atStart = j == 0
      val atEnd = j == numLines - height
      if (atStart) {
        drawTile(lineTile)(iRoot + width - 1, jRoot + 1)
      } else if (atEnd) {
        drawTile(lineTile)(iRoot + width - 1, jRoot + height - 2)
      } else {
        val jDraw = min(height - 3, max(2, (percentProgress*(height - 4)).toInt))
        drawTile(lineTile)(iRoot + width - 1, jRoot + jDraw)
      }
    }
  }
}
