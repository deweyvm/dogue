package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

case class TitleScreen(width:Int, height:Int, menu:Menu[Seq[Window]]) extends WindowContents {
  val title = Text.fromString(Color.Black, Color.White)("Dogue")
  override def outgoing:Map[WindowId,Seq[WindowMessage]] = Map()
  override def update(s:Seq[WindowMessage]): (Option[TitleScreen], Seq[Window]) = {
    val newWindows = menu.getResult.getOrElse(Seq())
    if (newWindows.length > 0) {
      (None, newWindows)
    } else {
      (copy(menu = menu.update).some, Seq())
    }

  }
  override def draw(r:WindowRenderer):WindowRenderer = {
    r <+| title.draw(width/2 - title.width/2, height/2) <+| menu.draw
  }
}
