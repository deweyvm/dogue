package com.deweyvm.whatever

import com.deweyvm.whatever.graphics.{GlyphFactory, Renderer}
import com.deweyvm.whatever.world.Stage
import com.deweyvm.whatever.input.Controls

class Engine {
  val glyphs = new GlyphFactory(16, 16, 16, 16, Assets.characterMap)
  var stage = Stage.create(glyphs)

  def update() {
    stage = stage.update
    Controls.update()
  }

  def draw() {
    stage.draw()
    Renderer.render()
  }
}
