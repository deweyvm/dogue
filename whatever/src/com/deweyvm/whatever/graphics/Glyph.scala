package com.deweyvm.whatever.graphics

import com.badlogic.gdx.graphics.g2d.Sprite
import com.deweyvm.gleany.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.deweyvm.gleany.AssetLoader
import com.deweyvm.gleany.data.Recti

//todo: makeText function?
class GlyphFactory(val rows:Int, val cols:Int, val tileWidth:Int, val tileHeight:Int, sheet:Texture) {
  private def makeSprite(index:Int, color:Color, texture:Texture) = {
    val x = index % rows
    val y = index / rows
    val region = AssetLoader.makeTextureRegion(texture, Some(Recti(x * tileWidth, y * tileHeight, tileWidth, tileHeight)))
    val sprite = new Sprite(region)
    sprite.setColor(color.toLibgdxColor)
    sprite
  }

  def makeGlyph(index:Int, color:Color) = {
    val sprite = makeSprite(index, color, sheet)
    new Glyph(index, sprite)
  }
}

class Glyph(index:Int, sprite:Sprite) {
  val width = sprite.getWidth
  val height = sprite.getHeight

  def draw(x:Float, y:Float) {
    Renderer.draw(sprite, x, y)
  }
}
