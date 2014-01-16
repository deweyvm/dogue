package com.deweyvm.whatever.ui

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.{Tile, Code}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.Assets

class ScrollBar(factory:GlyphFactory) {
  private def makeTile(code:Code):Tile =
    new Tile(Color.Black, Color.White, code.index, Assets.page437_16x16)

  val upArrow = makeTile(Code.▲)
  val downArrow = makeTile(Code.▼)
  val lineTile = makeTile(Code.─)

  def draw(numLines:Int, j:Int, width:Int, height:Int, iRoot:Int, jRoot:Int) {
    import scala.math._
    if (numLines > height) {
      upArrow.draw(iRoot + width - 1, jRoot)
      downArrow.draw(iRoot + width - 1, jRoot + height - 1)
      val percentProgress = j.toFloat/(numLines - height - 2)
      val atStart = j == 0
      val atEnd = j == numLines - height
      if (atStart) {
        lineTile.draw(iRoot + width - 1, jRoot + 1)
      } else if (atEnd) {
        lineTile.draw(iRoot + width - 1, jRoot + height - 2)
      } else {
        val jDraw = min(height - 3, max(2, (percentProgress*(height - 4)).toInt))
        lineTile.draw(iRoot + width - 1, jRoot + jDraw)
      }
    }
  }
}
