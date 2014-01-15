package com.deweyvm.whatever

import com.deweyvm.whatever.graphics.Renderer
import com.deweyvm.whatever.world.Stage
import com.deweyvm.whatever.input.Controls

class Engine {
  val stage = new Stage

  def update() {
    stage.update()
    Controls.update()
  }

  def draw() {
    stage.draw()
    Renderer.render()
  }
}
