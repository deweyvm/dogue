package com.deweyvm.whatever.ui

import com.deweyvm.whatever.input.Controls
import com.deweyvm.gleany.GleanyMath
import com.deweyvm.whatever.graphics.GlyphFactory

object TextView {
  def create(factory:GlyphFactory):TextView = new TextView(0, new ScrollBar(factory))
}



case class TextView(j:Int, scrollBar:ScrollBar) {

  def update(jMax:Int):TextView = {
    this.copy(j = GleanyMath.clamp(Controls.AxisY.justPressed + j, 0, jMax))
  }

  def drawText(text:Text, iRoot:Int, jRoot:Int) {
    text.letters.zipWithIndex map { case (tile, k) =>
      tile.draw(iRoot + k, jRoot)
    }
  }

  def draw(lines:Vector[Text], width:Int, height:Int, iRoot:Int, jRoot:Int) {
    scrollBar.draw(lines.length, j, width, height, iRoot, jRoot)
    for (k <- 0 until height) {
      val jj = k + j
      if (jj >= 0 && jj < lines.length) {
        val line = lines(jj)
        drawText(line, iRoot, jRoot + k)
      }
    }
  }
}
