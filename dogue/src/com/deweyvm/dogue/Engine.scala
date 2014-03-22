package com.deweyvm.dogue

import com.deweyvm.dogue.world._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.graphics.{WindowRenderer, OglRenderer}
import com.deweyvm.dogue.common.CommonImplicits._



class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  def cols = Game.settings.width.get
  def rows = Game.settings.height.get
  val factory = new WorkspaceFactory(cols, rows)

  var stage = makeStage

  def makeStage = {
    val pointer = Dogue.renderer match {
      case ogl:OglRenderer if true || ogl.vis.isDefined =>
        Pointer.create(
          factory.create
        )
      case _ =>
        Pointer.create(
          factory.create
        )
        /*Pointer.create(
          factory.create(Stage.World),
          factory.create(Stage.Chat)
        )*/
    }
    new WorkspaceManager(pointer)
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
    val renderer = WindowRenderer.create
    val rendered = stage.draw(renderer)
    Dogue.renderer.render(rendered).ignore()
  }
}
