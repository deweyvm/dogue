package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.common.data.control.Coroutine

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
        val mapWidth = WorldPanel.computeMapWidth(cols)
        val sideWidth = WorldPanel.computeSideWidth(cols)
        val controlRect = Recti(1, rows - WorldPanel.controlsHeight + 1, sideWidth, WorldPanel.controlsHeight - 1 - 1)
        val controlPanel = new Panel(controlRect, bgColor)
        val future:DogueFuture[Coroutine[WorldPanel]] = DogueFuture createAndRun { () => WorldPanel.getLoaders(cols, rows) }
        def createStage(panel:Panel) = makeStage(panel, controlPanel)
        val progressPanel = LoadingPanel.create(Recti(1, 1, cols - 2, rows - 2), bgColor, future, createStage)
        makeStage(progressPanel)

    }
  }
}

