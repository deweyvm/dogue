package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.graphics.WindowRenderer
import scala.collection.immutable.IndexedSeq

class WindowManager(screenCols:Int, screenRows:Int) {

  def updateWorkspace(windows:Vector[Window]):Vector[Window] = {
    val messages: Map[WindowId, Seq[WindowMessage]] = windows.map {_.getOutgoing}.foldLeft(Map[WindowId, Seq[WindowMessage]]()) { _ ++ _ }
    windows.map { w =>
      val myMessages: Seq[WindowMessage] = messages.get(w.id).getOrElse(Seq())
      val (self, newWindows) =  w.update(myMessages)
      newWindows ++ self.map{Seq(_)}.getOrElse(Seq[Window]())
    }.flatten
  }

  def draw(windows:Seq[Window])(r:WindowRenderer) = {
    val winDraws = windows.foldLeft(r) { (ren,window) =>
      (ren.at(window.rect.x, window.rect.y).move(1, 1) <+| window.draw).move(-1, -1) <+| drawBorder(window.rect)
    }
    val string = Client.instance.getStatus
    winDraws <+| Text.create(Color.Black, Color.White).append(string).draw(screenCols - string.length, screenRows - 1)
  }

  private def drawBorder(rect:Recti)(r:WindowRenderer) = {
    def makeTile(code:Code) = Tile(code, Color.Black, Color.White)
    val hTile = makeTile(Code.═)
    val vTile = makeTile(Code.║)
    val h = (0 until rect.width).map { i =>
      List((i, 0, hTile),
           (i, rect.height - 1, hTile))
    }.flatten
    val v = (0 until rect.height).map { j =>
      List((0, j, vTile),
           (rect.width - 1, j, vTile))
    }.flatten
    r <++ h <++ v <+
      (0,              0,               makeTile(Code.╔)) <+
      (rect.width - 1, 0,               makeTile(Code.╗)) <+
      (0,              rect.height - 1, makeTile(Code.╚)) <+
      (rect.width - 1, rect.height - 1, makeTile(Code.╝))

  }
}
