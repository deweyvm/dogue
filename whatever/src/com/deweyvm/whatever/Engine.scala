package com.deweyvm.whatever

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.deweyvm.whatever.graphics.{Renderer, Camera}
import com.deweyvm.whatever.world.Grid

class Engine {
  val grid = new Grid
  def update() {}
  def draw() {
    grid.draw()
    Renderer.render()
  }
}
