package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code

object Text {
  def create(bgColor:Color, fgColor:Color, factory:GlyphFactory):Text = {
    new Text("", bgColor, fgColor, factory)
  }

}

class Text(text:String, bgColor:Color, fgColor:Color, factory:GlyphFactory) {
  private val letters = text map { c =>
    val index = if (c.toInt > 255) {
      Code.unicodeToCode(c).index
    } else {
      c
    }
    new Tile(bgColor, fgColor, index, factory)
  }

  def width = letters.length

  def append(s:String):Text = {
    new Text(text + s, bgColor, fgColor, factory)
  }

  def setString(s:String):Text = {
    new Text(s, bgColor, fgColor, factory)
  }

  def draw(i:Int, j:Int) {
    filterDraw(i, j, {case (_:Int,_:Int) => true})
  }

  def filterDraw(i:Int, j:Int, f:(Int, Int) => Boolean) {
    letters.zipWithIndex map { case (tile, k) =>
      if (f(i + k, j)) {
        tile.draw(i + k, j)
      }
    }
  }
}

