package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client

trait StageType {
  def next:StageType
}


class StageFactory(cols:Int, rows:Int) {
  val serverText = Text.create(Color.Black, Color.White)
  val bgColor = Color.Blue
  private def makeStage(panels:Vector[Panel]) = {
    Stage(cols, rows, panels, serverText)
  }

  def create(t:StageType):Stage = {
    import Stage._
    t match {
      case Title =>
        val titlePanel = new TitlePanel(1, 1, cols - 2, rows - 2, bgColor)
        makeStage(Vector(titlePanel))
      case Chat =>
        val inputHeight = 2
        val bgColor = Color.Black
        val fgColor = Color.White
        val textInput = TextInput.create(TextInput.chat, cols - 2, inputHeight, bgColor, fgColor)
        val textOutput = InfoPanel.makeNew(1, 1, cols - 2, rows - 2 - inputHeight - 2, bgColor)//new TextOutput()

        val chatPanel = new ChatPanel(1, 1, cols - 2, rows - 2, bgColor, fgColor, Client.instance, textInput, textOutput)
        makeStage(Vector(chatPanel))
      case World =>
        val controlsHeight = 8
        val sideWidth = scala.math.min(cols/2 - 1, 24)
        val worldPanel = WorldPanel.create(0, 0, sideWidth + 2, 1, cols - sideWidth - 3, rows - 1 - 1, sideWidth, rows - controlsHeight - 1, bgColor, 4096, 4096)
        val controlPanel = new Panel(1, rows - controlsHeight + 1, sideWidth, controlsHeight - 1 - 1, bgColor)
        makeStage(Vector(worldPanel, controlPanel))

    }
  }
}
