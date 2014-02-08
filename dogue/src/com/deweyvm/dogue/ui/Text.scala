package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.Dogue

object Text {
  def create(bgColor:Color, fgColor:Color):Text = {
    new Text("", bgColor, fgColor)
  }

}

class Text(text:String, bgColor:Color, fgColor:Color) {
  private val letters = text map { c =>
    Tile(Code.unicodeToCode(c), bgColor, fgColor)
  }

  def width = letters.length

  def append(s:String):Text = {
    new Text(text + s, bgColor, fgColor)
  }

  def setString(s:String):Text = {
    new Text(s, bgColor, fgColor)
  }

  def draw(i:Int, j:Int) {
    filterDraw(i, j, {case (_:Int,_:Int) => true})
  }

  def filterDraw(i:Int, j:Int, f:(Int, Int) => Boolean) {
    letters.zipWithIndex foreach { case (tile, k) =>
      if (f(i + k, j)) {
        tile.draw(i + k, j)
      }
    }
  }
}

