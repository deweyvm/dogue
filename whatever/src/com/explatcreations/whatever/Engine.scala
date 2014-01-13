package com.explatcreations.whatever

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.explatcreations.whatever.graphics.Camera

class Engine {
  val font = Assets.font
  val batch = new SpriteBatch
  val camera = new Camera
  def update() {}
  def draw() {
    batch.begin()
    batch.setProjectionMatrix(camera.getProjection)
    font.draw(batch, "test", 0, 0)
    batch.end()
  }
}
