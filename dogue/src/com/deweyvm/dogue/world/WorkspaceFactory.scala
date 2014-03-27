package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.common.data.control.Coroutine
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.ui.world.WorldPanel
import com.deweyvm.dogue.Dogue

class WorkspaceFactory(screenCols:Int, screenRows:Int) {
  val wholeScreen = Recti(1, 1, screenCols-2, screenRows-2)
  val bgColor = Color.Blue
  private def makeWorkspace(panels:Window*) = {
    Workspace.create(screenCols, screenRows, panels.toVector)
  }

  val BlankContents = new WindowContents {
    override def outgoing: Map[WindowId, Seq[WindowMessage]] = Map()

    override def draw(r:WindowRenderer) = r

    override def update(s: Seq[WindowMessage]) = (this.some, Seq())
  }

  implicit class myWindow(c:WindowContents) {
    def toWindow(r:Recti) = c.makeWindow(r, bgColor)
  }

  private def createWorld = {
    val width = 40
    val worldWidth = screenCols - width - 4
    val output = TextPanel.create(width, Color.Blue, Color.White).toWindow(Recti(1, 1, width, screenRows - 2))
    def makeWindows(c:WindowContents) = {
      Seq(c.toWindow(Recti(width + 3, 1, worldWidth, screenRows - 2)),
          output)
    }
    val future =  DogueFuture.createAndRun(() => WorldPanel.getLoaders(screenCols, screenRows, output.id))
    val loadPanel = LoadingPanel.create(wholeScreen, Color.Blue, makeWindows, future)
    List(loadPanel.toWindow(wholeScreen))
  }

  private def createChat = {
    val inputHeight = 4
    val output = ChatPanel.create(Client.instance)(Text.fromString(bgColor, Color.White)).toWindow(Recti(1, 1, screenCols - 2, screenRows - inputHeight - 4))
    val input = NewTextInput.create(Client.instance.sourceName + "> ", bgColor, Color.White)
    val chatInput = ChatInput.create(input).addLink(output.id)
    List(output, chatInput.toWindow(Recti(1, screenRows - inputHeight - 1, screenCols - 2, inputHeight)))
  }

  def create:Vector[Workspace] = {

      val titleRect = wholeScreen

      val screen:TitleScreen = TitleScreen(titleRect.width, titleRect.height, TitleMenu.create(createWorld _))
      val titlePanel = screen.toWindow(titleRect)
      Vector(makeWorkspace(titlePanel), makeWorkspace(createChat:_*))

      /*case Stage.Chat =>
         val inputHeight = 3
         val bgColor = Color.Black
         val fgColor = Color.White
         val textInput = TextInput.create(TextInput.chat, screenCols - 2, inputHeight, bgColor, fgColor)
         val infoRect = Recti(1, 1, screenCols - 2, screenRows - 2 - inputHeight - 3)
         val infoPanel = InfoPanel.create()
         val textOutput = Window(infoRect, bgColor, infoPanel)
         val chatRect = Recti(1, 1, screenCols - 2, screenRows - 2)
         val chatPanel = new ChatWindow(chatRect, bgColor, fgColor, Client.instance, textInput, textOutput)
         makeStage(chatPanel)
       case Stage.World =>
         val sideWidth = WorldWindow.computeSideWidth(screenCols)
         val controlRect = Recti(1, screenRows - WorldWindow.controlsHeight + 1, sideWidth, WorldWindow.controlsHeight - 1 - 1)
         val controlPanel = new Window(controlRect, bgColor)
         val tooltipWidth = sideWidth
         val tooltipHeight = screenRows - WorldWindow.controlsHeight - 1
         val tooltip = InfoWindow.create(Recti(1, 1, tooltipWidth, tooltipHeight), bgColor)
         val future:DogueFuture[Coroutine[WorldWindow]] = DogueFuture createAndRun { () => WorldWindow.getLoaders(screenCols, screenRows) }
         def createStage(panel:Window) = makeStage(panel, controlPanel, tooltip)
         val progressPanel = LoadingWindow.create(Recti(1, 1, screenCols - 2, screenRows - 2), bgColor, future, createStage)
         makeStage(progressPanel)

    } */
  }
}

