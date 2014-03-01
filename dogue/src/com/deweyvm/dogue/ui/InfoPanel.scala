package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.gleany.data.Recti

object InfoPanel {
  def makeNew(rect:Recti, bgColor:Color):InfoPanel = {
    new InfoPanel(rect, bgColor, "", Vector(), new ScrollBar, 0)
  }
}

case class InfoPanel(override val rect:Recti,
                     bgColor:Color,
                     text:String,
                     lines:Vector[Text],
                     scrollBar:ScrollBar,
                     jView:Int)
  extends Panel(rect, bgColor) {
  private val leftMargin = 0
  private val rightMargin = 1
  private val topMargin = 0
  private val textWidth = width - leftMargin - rightMargin

  def addText(string:String, bgColor:Color, fgColor:Color):InfoPanel = {
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

  def addLines(lines:Vector[String], bgColor:Color, fgColor:Color):InfoPanel = {
    lines.foldLeft(this){case (acc, string) => acc.addText(string, bgColor, fgColor)}

  }



  private def atBottom:Boolean = lines.length <= height || jView < height

  private def updateView:InfoPanel = {
    val newJ = {
      if (lines.length < height) {
        lines.length - 1
      } else {
        (-Controls.AxisY.justPressed + jView).clamp(height - 1, lines.length - 1)
      }
    }
    this.copy(jView = newJ)
  }

  override def update:InfoPanel = {
    this.updateView
  }

  override def draw() {
    super.draw()
    drawText()
  }

  def drawText() {
    drawLines(lines, width, height, leftMargin + x, topMargin + y)
  }

  def drawText(text:Text, i:Int, j:Int) {
    text.filterDraw(i, j, contains)
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
}


