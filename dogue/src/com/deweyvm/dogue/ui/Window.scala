package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.{Point2d, Recti}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.dogue.graphics.{ColorScheme, WindowRenderer}

case class Window(rect:Recti, colors:ColorScheme, contents:WindowContents, id:WindowId) {
  val t = Tile(Code.` `, colors.bg, colors.bg)
  def contains(i:Int, j:Int):Boolean = rect.contains(Point2d(i,j))

  def getRect:Recti = rect
  def update(s:Seq[WindowMessage]) = {
    val (updated, newWindows) = contents.update(s)
    (updated map { c => copy(contents = c) }, newWindows)
  }
  def getOutgoing:Map[WindowId, Seq[WindowMessage]] = contents.outgoing

  private def drawBackground(r:WindowRenderer):WindowRenderer = {

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





