package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.{Point2d, Recti}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

trait WindowId
trait WindowMessage

trait WindowContents {
  self =>
  def outgoing:Map[WindowId, Seq[WindowMessage]]
  def update(s:Seq[WindowMessage]):Option[WindowContents]
  def spawnWindow:Option[Window]
  def draw(r:WindowRenderer):WindowRenderer

  /**
   *
   * @param rect the rect of the contents. the rect of the window will be larger by 2x2
   * @param bgColor the background color of the window
   * @return The created window and its associated ID
   */
  def makeWindow(rect:Recti, bgColor:Color):Window = {
    val id = new WindowId{}
    Window(rect, bgColor, this, id)
  }
}

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
    val xMin = rect.x
    val xMax = rect.x + rect.width
    val yMin = rect.y
    val yMax = rect.y + rect.height
    val draws = for (i <- xMin until xMax;
         j <- yMin until yMax) yield {
      (i, j, Tile(Code.` `, bgColor, bgColor))
    }
    r <++ draws
  }

  final def draw(r:WindowRenderer):WindowRenderer = {
    r <+| drawBackground <+| contents.draw
  }
}





