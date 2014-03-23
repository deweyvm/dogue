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

  def create:Workspace = {

      val titleRect = wholeScreen
      def makePopup() = {
        val output = TextPanel.create(Color.White, Color.Black).makeWindow(Recti(1, 1, screenCols-40, screenRows-40), Color.Red)
        val chat = TestChat.create(NewTextInput.create("what", Color.Black, Color.Red)).addLink(output.id)
        val chatWindow = chat.makeWindow(Recti(20,40,screenCols-40, screenRows-40), Color.White)
        List(output, chatWindow)
      }
      def makePopup2() = {
        val output = TextPanel.create(Color.Blue, Color.White).makeWindow(Recti(1, 1, 20, screenRows - 2), Color.White)
        def makeWindows(c:WindowContents) = {
          Seq(c.makeWindow(wholeScreen, Color.Blue),
              output)
        }
        List(LoadingPanel.create(wholeScreen, Color.Blue, makeWindows, DogueFuture.createAndRun(() => WorldPanel.getLoaders(screenCols, screenRows, output.id))).makeWindow(wholeScreen, Color.Blue))
      }
      val screen:TitleScreen = TitleScreen(titleRect.width, titleRect.height, TitleMenu.create(bgColor, makePopup2))
      val titlePanel = screen.makeWindow(titleRect, bgColor)
      makeWorkspace(titlePanel)

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

