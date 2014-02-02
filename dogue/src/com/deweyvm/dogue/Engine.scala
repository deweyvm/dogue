package com.deweyvm.dogue

import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.world.{StageFactory, Stage}
import com.deweyvm.dogue.input.Controls
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log


class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  val codePage = Assets.page437_16x16
  val cols = Game.RenderWidth/codePage.tileWidth
  val rows = Game.RenderHeight/codePage.tileHeight
  val factory = new StageFactory(cols, rows, codePage)
  var stage = factory.create(Stage.Chat)
  Log.info("Creating client named " + Client.instance.getName)
  def update() {
    stage = stage.update(factory)
    Controls.update()
    if (Controls.Escape.justPressed) {
      //
      Client.instance.disconnect()
      Gdx.app.exit()
    }
  }

  def draw() {
    stage.draw()
    Renderer.render()
  }
}
