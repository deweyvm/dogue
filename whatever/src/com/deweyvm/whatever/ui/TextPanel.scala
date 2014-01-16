package com.deweyvm.whatever.ui

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color

object TextPanel {
  def makeNew(x:Int, y:Int, width:Int, height:Int, factory:GlyphFactory):TextPanel = {
    new TextPanel(x, y, width, height, factory, "", Vector(), TextView.create(factory))
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

case class TextPanel(override val x:Int,
                     override val y:Int,
                     override val width:Int,
                     override val height:Int,
                     factory:GlyphFactory, text:String, lines:Vector[Text], view:TextView)
  extends Panel(x, y, width, height) {
  private val leftMargin = 0
  private val rightMargin = 1
  private val topMargin = 0
  private val textWidth = width - leftMargin - rightMargin
  def addText(string:String, bgColor:Color, fgColor:Color):TextPanel = {
    val addedLines = TextPanel.splitText(string, textWidth) map { s =>
      new Text(s, bgColor, fgColor, factory)
    }
    this.copy(text = text + string,
              lines = lines ++ addedLines)
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
    this.copy(view = view.update(lines.length - height))
  }

  override def draw() {
    super.draw()
    view.draw(lines, width, height, leftMargin + x, topMargin + y)
  }
}


