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
        val messagePanel = InfoPanel.makeNew(1, 1, cols/2 - 1 - 1, rows - 8 - 1, bgColor).
          addText("abc1", Color.White, Color.Black)
        val worldPanel = WorldPanel.create(0, 0, cols/2, 1, cols/2 - 1, rows - 1 - 1, bgColor, 50, 50)
        val controlPanel = new Panel(1, rows - controlsHeight + 1, cols/2 - 1 - 1, controlsHeight - 1 - 1, bgColor)
        makeStage(Vector(messagePanel, worldPanel, controlPanel))

    }
  }
}
