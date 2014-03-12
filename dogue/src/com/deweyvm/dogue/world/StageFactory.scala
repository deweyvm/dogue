package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.Implicits
import Implicits._

class StageFactory(cols:Int, rows:Int) {
  val serverText = Text.create(Color.Black, Color.White)
  val bgColor = Color.Blue
  private def makeStage(panels:Panel*) = {
    Stage(cols, rows, panels.toVector, serverText)
  }

  def create(t:StageType):Stage = {
    import Stage._
    t match {
      case Blank =>
        val blankRect = Recti(1, 1, cols - 2, rows - 2)
        makeStage(new Panel(blankRect, Color.Black))
      case Title =>
        val titleRect = Recti(1, 1, cols - 2, rows - 2)
        val titlePanel = TitlePanel.create(titleRect, this, bgColor)
        makeStage(titlePanel)
      case Chat =>
        val inputHeight = 3
        val bgColor = Color.Black
        val fgColor = Color.White
        val textInput = TextInput.create(TextInput.chat, cols - 2, inputHeight, bgColor, fgColor)
        val infoRect = Recti(1, 1, cols - 2, rows - 2 - inputHeight - 3)
        val textOutput = InfoPanel.makeNew(infoRect, bgColor)//new TextOutput()
        val chatRect = Recti(1, 1, cols - 2, rows - 2)
        val chatPanel = new ChatPanel(chatRect, bgColor, fgColor, Client.instance, textInput, textOutput)
        makeStage(chatPanel)
      case World =>
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
        val worldPanel = WorldPanel.create(mapRect, sideWidth, rows - controlsHeight - 1, minimapSize, bgColor, worldSize)
        val controlRect = Recti(1, rows - controlsHeight + 1, sideWidth, controlsHeight - 1 - 1)
        val controlPanel = new Panel(controlRect, bgColor)
        makeStage(worldPanel, controlPanel)

    }
  }
}
