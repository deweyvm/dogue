package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.common.data.control.Coroutine
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.{ColorScheme, WindowRenderer}
import com.deweyvm.dogue.ui.world.WorldPanel
import com.deweyvm.dogue.Dogue

class WorkspaceFactory(screenCols:Int, screenRows:Int) {
  val wholeScreen = Recti(1, 1, screenCols-2, screenRows-2)
  val colors = new ColorScheme(Color.Purple, Color.DarkGrey, Color.White, Color.Black, Color.Red)
  private def makeWorkspace(panels:Window*) = {
    Workspace.create(screenCols, screenRows, panels.toVector)
  }

  val BlankContents = new WindowContents {
    override def outgoing: Map[WindowId, Seq[WindowMessage]] = Map()

    override def draw(r:WindowRenderer) = r

    override def update(s: Seq[WindowMessage]) = (this.some, Seq())
  }

  implicit class myWindow(c:WindowContents) {
    def toWindow(r:Recti) = c.makeWindow(r, colors)
  }

  private def createWorld: List[Window] = {
    val width = 40
    val worldWidth = screenCols - width - 4
    val output = TextPanel.create(width, colors).toWindow(Recti(1, 1, width, screenRows - 2))
    def makeWindows(c:WindowContents) = {
      Seq(c.toWindow(Recti(width + 3, 1, worldWidth, screenRows - 2)),
          output)
    }
    val future =  DogueFuture.createAndRun(() => WorldPanel.getLoaders(screenCols, screenRows, output.id))
    val loadPanel = LoadingPanel.create(wholeScreen, colors, makeWindows, future)
    List(loadPanel.toWindow(wholeScreen))
  }

  private def createChat: List[Window] = {
    val inputHeight = 4
    val output = ChatPanel.create(Client.instance)(colors.makeText).toWindow(Recti(1, 1, screenCols - 2, screenRows - inputHeight - 4))
    val input = NewTextInput.create(Client.instance.sourceName + "> ", colors)
    val chatInput = ChatInput.create(input).addLink(output.id)
    List(output, chatInput.toWindow(Recti(1, screenRows - inputHeight - 1, screenCols - 2, inputHeight)))
  }

  private def createDungeon: List[Window] = {
    List()
  }


  def create:Vector[Workspace] = {
      val titleMenu = TitleMenu.create(colors, createWorld _, createDungeon _)
      val titleRect = wholeScreen

      val screen:TitleScreen = TitleScreen(titleRect.width, titleRect.height, titleMenu)
      val titlePanel = screen.toWindow(titleRect)
      Vector(makeWorkspace(titlePanel), makeWorkspace(createChat:_*))


  }
}

