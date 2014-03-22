package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.{Point2d, Recti}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

case class Window(rect:Recti, bgColor:Color, contents:WindowContents, id:WindowId) {

  def contains(i:Int, j:Int):Boolean = rect.contains(Point2d(i,j))

  def getRect:Recti = rect
  def update(s:Seq[WindowMessage]) = {
    val updated = contents.update(s)
    updated map { c => copy(contents = c) }
  }
  def getOutgoing:Map[WindowId, Seq[WindowMessage]] = contents.outgoing
  def spawnWindow:Option[Window] = contents.spawnWindow

  private def drawBackground(r:WindowRenderer):WindowRenderer = {
    val t = Tile(Code.` `, bgColor, bgColor)
    val draws = for (i <- 0 until rect.width - 2;
                     j <- 0 until rect.height - 2) yield {
      (i, j, t)
    }
    r <++ draws
  }

  final def draw(r:WindowRenderer):WindowRenderer = {
    r <+| drawBackground <+| contents.draw
  }
}





