package com.deweyvm.whatever.graphics

import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, SpriteBatch}
import scala.collection.mutable.ArrayBuffer

object Renderer {
  private val batch = new SpriteBatch
  private val camera = new Camera
  private val draws = ArrayBuffer[() => Unit]()

  def draw(f:BitmapFontCache) {
    draws += (() => f.draw(batch))
  }

  def render() {
    batch.begin()
    batch.setProjectionMatrix(camera.getProjection)
    draws foreach {_()}
    draws.clear()
    batch.end()
  }
}
