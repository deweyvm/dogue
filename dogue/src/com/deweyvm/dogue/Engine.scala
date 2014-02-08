package com.deweyvm.dogue

import com.deweyvm.dogue.world.{StageManager, StageFactory, Stage}
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.common.procgen.PerlinNoise
import com.deweyvm.gleany.graphics.ImageUtils


class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  val cols = Game.RenderWidth/Dogue.tileSpec.width
  val rows = Game.RenderHeight/Dogue.tileSpec.height
  val factory = new StageFactory(cols, rows)
  val default = PerlinNoise.default
  ImageUtils.saveHeight(default.render.elements, default.size, default.size, "test.png")
  Game.shutdown()

  var stage = new StageManager(Pointer.create(
    factory.create(Stage.World),
    factory.create(Stage.Chat),
    factory.create(Stage.Title)


  ))
  Log.info("Creating client named " + Client.instance.sourceName)
  def update() {
    stage = stage.update
    Controls.update()
    if (Controls.Escape.justPressed) {
      Game.shutdown()
    }
  }

  def draw() {
    stage.draw()
    Dogue.renderer.render()
  }
}
