package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.gleany.data.Recti

/*object InfoPanel {
  def create:InfoPanel = {
    new InfoPanel("", Vector(), new ScrollBar, 0)
  }
}



case class InfoPanel(text:String,
                     lines:Vector[Text],
                     scrollBar:ScrollBar,
                     jView:Int) extends WindowContents {
  private val leftMargin = 0
  private val rightMargin = 1
  private val topMargin = 0
  private val textWidth = width - leftMargin - rightMargin
  def outgoing = ()
  def update(s:Seq[WindowMessage]):InfoPanel = {
    val added = lines.foldLeft(this){case (acc, (bg, fg, string)) => acc.addText(string, bgColor, fgColor)}
    added.updateView
  }
  def draw() {
    drawText()
  }

  private def atBottom:Boolean = lines.length <= height || jView < height

  private def updateView:InfoPanel= {
    val newJ = {
      if (lines.length < height) {
        lines.length - 1
      } else {
        (-Controls.AxisY.justPressed + jView).clamp(height - 1, lines.length - 1)
      }
    }
    this.copy(jView = newJ)
  }

  private def drawLines(lines:Vector[Text], width:Int, height:Int, iRoot:Int, jRoot:Int) {
    scrollBar.draw(lines.length, lines.length - jView - 1, width, height, iRoot, jRoot)
    for (k <- 0 until height) {
      val jj = k + (lines.length - jView - 1)
      if (jj >= 0 && jj < lines.length) {
        val line = lines(jj)
        line.draw(iRoot, jRoot + k)
      }
    }
  }


  private def addText(string:String, bgColor:Color, fgColor:Color):InfoPanel = {
    val addedLines = string.toLines(textWidth) map { s =>
      Text.fromString(s, bgColor, fgColor)
    }

    val newjView = if (!atBottom) {
      jView + addedLines.length
    } else {
      jView
    }
    this.copy(text = text + string,
              lines = lines ++ addedLines,
              jView = newjView)
  }

  def drawText() {
    drawLines(lines, width, height, leftMargin + x, topMargin + y)
  }

  def drawText(text:Text, i:Int, j:Int) {
    text.filterDraw(i, j, contains)
  }
}*/

