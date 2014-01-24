package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.{Texture, Pixmap}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.deweyvm.gleany.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite

object RectSprite {
  private val texture = makeTexture

  private def makeTexture = {
    val pixmap = new Pixmap(1, 1, Format.RGBA8888)
    pixmap.setColor(Color.White.toLibgdxColor)
    pixmap.fill()
    val result = new Texture(pixmap)
    pixmap.dispose()
    result
  }

  private def makeSprite(width: Int, height: Int, color: Color) = {
    val result = new Sprite(texture)
    result.setOrigin(0, 0)
    result.setScale(width, height)
    result.setColor(color.toLibgdxColor)
    result
  }
}

class RectSprite(val width: Int, val height: Int, color: Color) {
  import RectSprite._

  private val sprite = makeSprite(width, height, color)

  def update() {}

  def setAlpha(a: Float): RectSprite = {
    sprite.setColor(color.copy(a = a).toLibgdxColor)
    this
  }

  def setSize(width: Int, height: Int): RectSprite = {
    new RectSprite(width, height, color)
  }

  def draw(x: Float, y: Float) {
    Renderer.draw(sprite, x, y)
  }
}

