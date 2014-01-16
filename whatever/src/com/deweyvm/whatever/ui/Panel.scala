package com.deweyvm.whatever.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.data.Recti

class Panel(val x:Int, val y:Int, val width:Int, val height:Int) {
  def contains(i:Int, j:Int):Boolean =
    i >= x && i < x + width && j >= y && j < y + height

  def getRect:Recti = Recti(x, y, width, height)
  def update():Panel = this
  def draw() { }
}


object TextPanel {
  def makeNew(x:Int, y:Int, width:Int, height:Int, factory:GlyphFactory):TextPanel = {
    new TextPanel(x, y, width, height, factory, "", Vector())
  }

  def splitText(string:String, textWidth:Int):Vector[String] = {
    val (last, lines) = string.foldLeft(("", Vector[String]())){
      case ((currentLine, lines), c) =>
        val added = currentLine + c
        if (added.length == textWidth - 1) {
          val hyphen = if (c == ' ') "" else  "-"
          ("", lines ++ Vector(added + hyphen))
        } else {
          (added, lines)
        }
    }
    lines ++ Vector(last)
  }
}

class TextPanel(x:Int, y:Int, width:Int, height:Int, factory:GlyphFactory, text:String, lines:Vector[Text]) extends Panel(x, y, width, height) {
  private val leftMargin = 0
  private val rightMargin = 0
  private val topMargin = 0
  private val textWidth = width - leftMargin - rightMargin
  def addText(string:String, bgColor:Color, fgColor:Color):TextPanel = {
    val addedLines = TextPanel.splitText(string, textWidth) map { s =>
      new Text(s, bgColor, fgColor, factory)
    }
    new TextPanel(x, y, width, height, factory, text + string, lines ++ addedLines)
  }

  def drawText(text:Text, i:Int, j:Int) {
    text.letters.zipWithIndex map { case (tile, k) =>
      val ii = i + k
      if (contains(ii, j)) {
        tile.draw(i + k, j)
      }
    }
  }

  override def update():TextPanel = {
    this
  }

  override def draw() {
    lines.zipWithIndex foreach { case(line,j) =>
      drawText(line, leftMargin + x, topMargin + y + j)
    }
  }
}
