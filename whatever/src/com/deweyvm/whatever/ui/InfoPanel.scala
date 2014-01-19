package com.deweyvm.whatever.ui

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color

object InfoPanel {
  def makeNew(x:Int, y:Int, width:Int, height:Int, factory:GlyphFactory):InfoPanel = {
    new InfoPanel(x, y, width, height, factory, "", Vector(), TextView.create(factory))
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

case class InfoPanel(override val x:Int,
                     override val y:Int,
                     override val width:Int,
                     override val height:Int,
                     factory:GlyphFactory, text:String, lines:Vector[Text], view:TextView, ctr:Int=0)
  extends Panel(x, y, width, height) {
  private val leftMargin = 0
  private val rightMargin = 1
  private val topMargin = 0
  private val textWidth = width - leftMargin - rightMargin

  def addText(string:String, bgColor:Color, fgColor:Color):InfoPanel = {
    val addedLines = InfoPanel.splitText(string, textWidth) map { s =>
      new Text(s, bgColor, fgColor, factory)
    }
    this.copy(text = text + string,
              lines = lines ++ addedLines)
  }

  def drawText(text:Text, i:Int, j:Int) {
    text.filterDraw(i, j, contains)
    /*text.letters.zipWithIndex map { case (tile, k) =>
      val ii = i + k
      if (contains(ii, j)) {
        tile.draw(i + k, j)
      }
    }*/
  }

  override def update():InfoPanel = {
    val (addLine, newCtr) =
      if (ctr >= 180) {
        (true, 0)
      } else {
        (false, ctr + 1)
      }
    val next = this.copy(view = view.update(lines.length - height), ctr = newCtr)
    if (addLine) {
      next.addText("this is an added line", Color.White, Color.Black)
    } else {
      next
    }
  }

  override def draw() {
    super.draw()
    view.draw(lines, width, height, leftMargin + x, topMargin + y)
  }
}


