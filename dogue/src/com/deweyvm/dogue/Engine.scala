package com.deweyvm.dogue

import com.deweyvm.dogue.world._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.common.procgen.{MapName, PerlinNoise}
import com.deweyvm.gleany.graphics.ImageUtils
import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.gleany.graphics.display.Display


class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  def cols = Game.RenderWidth/Dogue.tileSpec.width
  def rows = Game.RenderHeight/Dogue.tileSpec.height
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

    Controls.AxisX.justPressed match {
      case 0 => ()
      case x => Display.resize(16*x, 0)
    }
    Controls.AxisY.justPressed match {
      case 0 => ()
      case y => Display.resize(0, 16*y)
    }
  }

  def draw() {
    stage.draw()
    Dogue.renderer.render()
  }
}
