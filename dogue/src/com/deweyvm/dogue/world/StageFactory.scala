package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.procgen.PerlinParams
import com.deweyvm.dogue.world.Stage.Chat
import com.deweyvm.dogue.common.threading.DogueFuture

class StageFactory(cols:Int, rows:Int) {
  val serverText = Text.create(Color.Black, Color.White)
  val bgColor = Color.Blue
  private def makeStage(panels:Panel*) = {
    Stage(cols, rows, panels.toVector, serverText)
  }

  def create(t:StageType):Stage = {
    t match {
      case Stage.Blank =>
        val blankRect = Recti(1, 1, cols - 2, rows - 2)
        makeStage(new Panel(blankRect, Color.Black))
      case Stage.Title =>
        val titleRect = Recti(1, 1, cols - 2, rows - 2)
        val titlePanel = TitlePanel.create(titleRect, this, bgColor)
        makeStage(titlePanel)
      case Stage.Chat =>
        val inputHeight = 3
        val bgColor = Color.Black
        val fgColor = Color.White
        val textInput = TextInput.create(TextInput.chat, cols - 2, inputHeight, bgColor, fgColor)
        val infoRect = Recti(1, 1, cols - 2, rows - 2 - inputHeight - 3)
        val textOutput = InfoPanel.makeNew(infoRect, bgColor)//new TextOutput()
        val chatRect = Recti(1, 1, cols - 2, rows - 2)
        val chatPanel = new ChatPanel(chatRect, bgColor, fgColor, Client.instance, textInput, textOutput)
        makeStage(chatPanel)
      case Stage.World =>
        val worldSize = 256
        val controlsHeight = 8
        val minSideWidth = 24
        val minimapSize = 69
        val maxMinimapPanelSize = minimapSize + 3
        val (sideWidth, mapWidth) =
          if (cols > minSideWidth + maxMinimapPanelSize) {
            (cols - maxMinimapPanelSize, maxMinimapPanelSize)
          } else {
            (minSideWidth, cols - minSideWidth)
          }
        val mapRect = Recti(sideWidth + 2, 1, mapWidth - 3, rows - 2)

        val future = DogueFuture.runProgress(() => {
          val seed = 0//System.nanoTime
          println("Seed: " + seed + "L")
          val date = DateConstants(framesPerDay = 60*60*24*60)
          val perlin = PerlinParams(worldSize/4, 8, worldSize, seed)
          val params = WorldParams(minimapSize, perlin, date)
          val eco = EcosphereLoader.create(params)
          val world = World.create(params, eco)

          WorldPanel.create(mapRect, sideWidth, rows - controlsHeight - 1, minimapSize, bgColor, worldSize, world, params)
        }, () => 0.0)
        val progressPanel = new LoadingPanel(Recti(1, 1, cols - 2, rows - 2), bgColor, future)
        val controlRect = Recti(1, rows - controlsHeight + 1, sideWidth, controlsHeight - 1 - 1)
        val controlPanel = new Panel(controlRect, bgColor)
        makeStage(controlPanel, progressPanel)

    }
  }
}
