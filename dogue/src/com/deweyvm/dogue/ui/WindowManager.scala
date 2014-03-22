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
    val updatedWindows: Vector[Window] = windows.map { w =>
      val myMessages: Option[Seq[WindowMessage]] = messages.get(w.id)
      myMessages map w.update getOrElse w.update(Seq())
    }.flatten
    val newWindows: Vector[Window] = windows.map {_.spawnWindow}.flatten
   updatedWindows ++ newWindows
  }

  def draw(windows:Seq[Window])(r:WindowRenderer) = {
    val winDraws = windows.foldLeft(r) { (ren,window) =>
      ren <+| window.draw <+| drawBorder(window.rect)
    }
    val string = Client.instance.getStatus
    winDraws <+| Text.create(Color.Black, Color.White).append(string).draw(screenCols - string.length, screenRows - 1)
  }

  private def drawBorder(rect:Recti)(r:WindowRenderer) = {
    def makeTile(code:Code) = Tile(code, Color.Black, Color.White)
    val hTile = makeTile(Code.═)
    val vTile = makeTile(Code.║)
    val h = (rect.x + 1 until rect.right).map { i =>
      List((i, rect.y, hTile),
           (i, rect.bottom, hTile))
    }.flatten
    val v = (rect.y + 1 until rect.bottom).map { j =>
      List((rect.x, j, vTile),
           (rect.right, j, vTile))
    }.flatten
    r <++ h <++ v <+
      (rect.x, rect.y, makeTile(Code.╔)) <+
      (rect.right, rect.y, makeTile(Code.╗)) <+
      (rect.x, rect.bottom, makeTile(Code.╚)) <+
      (rect.right, rect.bottom, makeTile(Code.╝))

  }
}
