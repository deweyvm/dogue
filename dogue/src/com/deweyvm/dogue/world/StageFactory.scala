package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti

trait StageType {
  def next:StageType
}


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
        val titlePanel = new TitlePanel(titleRect, bgColor)
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
        val controlsHeight = 8
        val sideWidth = scala.math.min(cols/2 - 1, 24)
        val loc = Recti(sideWidth + 2, 1, cols - sideWidth - 3, rows - 1 - 1)
        val worldPanel = WorldPanel.create(loc, sideWidth, rows - controlsHeight - 1, bgColor, 4096*16)
        val controlRect = Recti(1, rows - controlsHeight + 1, sideWidth, controlsHeight - 1 - 1)
        val controlPanel = new Panel(controlRect, bgColor)
        makeStage(worldPanel, controlPanel)

    }
  }
}
