package com.deweyvm.whatever.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.entities.Tile

class Text(text:String, bgColor:Color, fgColor:Color, factory:GlyphFactory) {
  val letters = text map { c =>
    val index = if (c.toInt > 255) {
      '?'.toInt
    } else {
      c
    }
    new Tile(bgColor, fgColor, index, factory)
  }
}

class Menu {

}
