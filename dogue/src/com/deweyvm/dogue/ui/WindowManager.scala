package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client

class WindowManager(screenCols:Int, screenRows:Int) {

  def updateWorkspace(windows:Vector[Window]):Vector[Window] = {
    val messages: Map[WindowId, Seq[WindowMessage]] = windows.map {_.getOutgoing}.foldLeft(Map[WindowId, Seq[WindowMessage]]()) { _ ++ _ }
    val updatedWindows: Vector[Window] = windows.map { w =>
      val myMessages: Option[Seq[WindowMessage]] = messages.get(w.id)
      myMessages map w.update getOrElse w.update(Seq())
    }.flatten
    val newWindows: Vector[Window] = windows.map {_.spawnWindow}.flatten
   updatedWindows ++ newWindows
  }

  def draw(windows:Seq[Window]) {

    windows foreach { window =>
      window.draw()
      drawBorder(window.rect)
    }
    val string = Client.instance.getStatus
    Text.create(Color.Black, Color.White).append(string).draw(screenCols - string.length, screenRows - 1)
  }

  private def drawBorder(rect:Recti) {
    def makeTile(code:Code) = Tile(code, Color.Black, Color.White)
    (rect.x + 1 until rect.right) foreach { i =>
      makeTile(Code.═).draw(i, rect.y)
      makeTile(Code.═).draw(i, rect.bottom)
    }
    (rect.y + 1 until rect.bottom) foreach { j =>
      makeTile(Code.║).draw(rect.x, j)
      makeTile(Code.║).draw(rect.right, j)
    }
    makeTile(Code.╔).draw(rect.x, rect.y)
    makeTile(Code.╗).draw(rect.right, rect.y)
    makeTile(Code.╚).draw(rect.x, rect.bottom)
    makeTile(Code.╝).draw(rect.right, rect.bottom)
  }
}
