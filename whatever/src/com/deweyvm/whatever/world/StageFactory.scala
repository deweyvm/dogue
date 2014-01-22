package com.deweyvm.whatever.world

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.whatever.ui._
import com.deweyvm.gleany.graphics.Color

trait StageType {
  def next:StageType
}


class StageFactory(cols:Int, rows:Int, factory:GlyphFactory) {
  val serverText = Text.create(Color.Black, Color.White, factory)
  val bgColor = Color.Blue
  private def makeStage(t:StageType, panels:Vector[Panel]) = {
    Stage(t, cols, rows, factory, panels, serverText)
  }

  def create(t:StageType):Stage = {
    import Stage._
    t match {
      case Title =>
        val titlePanel = new TitlePanel(1, 1, cols - 2, rows - 2, bgColor, factory)
        makeStage(t, Vector(titlePanel))
      case Chat =>
        val inputHeight = 2
        val textInput = TextInput.create("such text: ", cols - 2, inputHeight, Color.Black, Color.White, factory)
        val chatPanel = new Panel(1, 1, cols - 2, rows - 2 - inputHeight - 1, bgColor)
        val inputPanel = new InputPanel(1, rows - 1 - inputHeight, cols - 2, inputHeight, bgColor, factory, textInput)
        makeStage(t, Vector(chatPanel, inputPanel))
      case World =>
        val controlsHeight = 8
        val messagePanel = InfoPanel.makeNew(1, 1, cols/2 - 1 - 1, rows - 8 - 1, bgColor, factory)
        val worldPanel = WorldPanel.create(0, 0, cols/2, 1, cols/2 - 1, rows - 1 - 1, bgColor, 50, 50, factory)
        val controlPanel = new Panel(1, rows - controlsHeight + 1, cols/2 - 1 - 1, controlsHeight - 1 - 1, bgColor)
        makeStage(t, Vector(messagePanel, worldPanel, controlPanel))

    }
  }
}
