package com.deweyvm.whatever

import com.deweyvm.whatever.graphics.{GlyphFactory, Renderer}
import com.deweyvm.whatever.world.Stage
import com.deweyvm.whatever.input.Controls

class Engine {
  val codePage = Assets.page437_16x16
  val cols = Game.RenderWidth/codePage.tileWidth
  val rows = Game.RenderHeight/codePage.tileHeight
  var stage = Stage.create(codePage, cols, rows)

  def update() {
    stage = stage.update
    Controls.update()
  }

  def draw() {
    stage.draw()
    Renderer.render()
  }
}
