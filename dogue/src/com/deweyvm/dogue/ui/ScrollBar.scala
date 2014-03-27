package com.deweyvm.dogue.ui

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
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
    Tile(code, Color.Black, Color.White)


  private val upArrow = makeTile(Code.▲)
  private val downArrow = makeTile(Code.▼)
  private val lineTile = makeTile(Code.─)

  def draw(numLines:Int, j:Int, width:Int, height:Int, iRoot:Int, jRoot:Int)(r:WindowRenderer):WindowRenderer = {
    import scala.math._
    def drawLine = {
      val percentProgress = j.toFloat/(numLines - height - 2)
      val atStart = j == 0
      val atEnd = j == numLines - height
      if (atStart) {
        (iRoot + width - 1, jRoot + 1, lineTile)
      } else if (atEnd) {
        (iRoot + width - 1, jRoot + height - 2, lineTile)
      } else {
        val jDraw = min(height - 3, max(2, (percentProgress*(height - 4)).toInt))
        (iRoot + width - 1, jRoot + jDraw, lineTile)
      }
    }
    val draw = (numLines > height).partial(
      r <+
        (iRoot + width - 1, jRoot, upArrow) <+
        (iRoot + width - 1, jRoot + height - 1, downArrow) <+~
        drawLine
    )
    draw.getOrElse(r)
  }
}
