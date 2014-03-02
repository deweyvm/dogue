package com.deweyvm.dogue

import com.deweyvm.dogue.world._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.common.procgen.MapName
import com.deweyvm.dogue.graphics.OglRenderer


class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  def cols = Game.settings.width.get
  def rows = Game.settings.height.get
  val factory = new StageFactory(cols, rows)

  var stage = makeStage

  def makeStage = {
    val pointer = Dogue.renderer match {
      case ogl:OglRenderer if ogl.vis.isDefined =>
        Pointer.create(
          factory.create(Stage.Blank)
        )
      case _ =>
        Pointer.create(
          factory.create(Stage.World),
          factory.create(Stage.Chat)
        )
    }
    new StageManager(pointer)
  }

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
