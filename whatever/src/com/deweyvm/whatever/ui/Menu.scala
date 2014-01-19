package com.deweyvm.whatever.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.Tile

class Text(text:String, bgColor:Color, fgColor:Color, factory:GlyphFactory) {
  private val letters = text map { c =>
    val index = if (c.toInt > 255) {
      '?'.toInt
    } else {
      c
    }
    new Tile(bgColor, fgColor, index, factory)
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

