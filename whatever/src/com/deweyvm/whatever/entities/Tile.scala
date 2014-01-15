package com.deweyvm.whatever.entities

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.{GlyphFactory, RectSprite, Glyph}
import com.deweyvm.whatever.Assets

class Tile(bgColor:Color, fgColor:Color, index:Int, factory:GlyphFactory) {


  val width = factory.tileWidth
  val height = factory.tileHeight
  val character = factory.makeGlyph(index, fgColor)

  val bg = new RectSprite(width, height, bgColor)

  def draw(i:Int, j:Int) {
    val x = i*width
    val y = j*height
    bg.draw(x, y)
    character.draw(x, y)
  }
}
