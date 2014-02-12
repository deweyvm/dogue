package com.deweyvm.dogue

import com.deweyvm.dogue.world._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.common.procgen.{MapName, PerlinNoise}
import com.deweyvm.gleany.graphics.ImageUtils


class Engine {
  TextInput.addListener()//put this somewhere more reasonable
  val cols = Game.RenderWidth/Dogue.tileSpec.width
  val rows = Game.RenderHeight/Dogue.tileSpec.height
  val factory = new StageFactory(cols, rows)
  val start = System.nanoTime()
  val iters = 1
  /*(0 until iters) foreach { _ =>fl
    val size = 512
    val world = new World(WorldParams(128, 7, size, System.nanoTime.toInt)).tiles.strictGetAll map { _.height

    }
    ImageUtils.saveHeight(world, size, size, "test.png")
    ()
  }
  val end = (System.nanoTime() - start)/(iters*1000000L)
  println(end + " ms")
  //
  Game.shutdown()*/
  var stage = new StageManager(Pointer.create(
    factory.create(Stage.Title),
    factory.create(Stage.World),
    factory.create(Stage.Chat)


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
