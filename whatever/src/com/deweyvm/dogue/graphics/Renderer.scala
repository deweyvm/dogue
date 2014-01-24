package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.g2d.{Sprite, BitmapFontCache, SpriteBatch}
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.Gdx

object Renderer {
  private val batch = new SpriteBatch
  private val camera = new Camera
  private val draws = ArrayBuffer[() => Unit]()

  def draw(f:BitmapFontCache) {
    draws += (() => f.draw(batch))
  }

  def draw(s:Sprite, x:Float, y:Float) {
    draws += (() => {
      s.setPosition(x, y)
      s.draw(batch)
    })
  }

  def render() {
    batch.begin()
    batch.setProjectionMatrix(camera.getProjection)
    draws foreach {_()}
    draws.clear()
    batch.end()
  }
}
