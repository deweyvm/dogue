package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.common.data.control.Coroutine
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

class StageFactory(screenCols:Int, screenRows:Int) {
  val wholeScreen = Recti(0, 0, screenCols, screenRows)
  val bgColor = Color.Blue
  private def makeStage(panels:Window*) = {
    Workspace.create(screenCols, screenRows, panels.toVector)
  }

  val BlankContents = new WindowContents {
    override def spawnWindow: Option[Window] = None

    override def outgoing: Map[WindowId, Seq[WindowMessage]] = Map()

    override def draw(): Unit = ()

    override def update(s: Seq[WindowMessage]) = this.some
  }

  def create(t:StageType):Workspace = {
    t match {
      case Stage.Blank =>
        val blankRect = wholeScreen
        makeStage(new Window(blankRect, Color.Black, BlankContents, new WindowId{}))

      case Stage.Title =>
        val titleRect = wholeScreen
        def makePopup() = {
          Window(Recti(20,20,screenCols-40, screenRows-40), bgColor, BlankContents, new WindowId{})
        }
        val screen:TitleScreen = TitleScreen(titleRect.width, titleRect.height, TitleMenu.create(bgColor,makePopup ))
        val titlePanel = Window(titleRect, bgColor, screen, new WindowId{})
        makeStage(titlePanel)
      case _ => throw new RuntimeException
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
 */
    }
  }
}

