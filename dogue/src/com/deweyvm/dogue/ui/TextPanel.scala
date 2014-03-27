package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.ui.WindowMessage.TextMessage

object TextPanel {
  def create(width:Int, bgColor:Color, fgColor:Color) = new TextPanel(width, bgColor, fgColor, Vector())
}

case class TextPanel(width:Int, bgColor:Color, fgColor:Color, text:Vector[Text]) extends WindowContents {
  override def update(s: Seq[WindowMessage]): (Option[TextPanel], Seq[Window]) = {
    val doClear = s.contains(WindowMessage.Clear)
    val strings = s.map {
      case WindowMessage.TextMessage(str) => str.toLines(width).map{Text.fromString(bgColor, fgColor)}.some
      case _ => None
    }.flatten.flatten.toVector
    val newText = if (doClear) {
      strings
    } else {
      text ++ strings
    }
    val self = copy(text = newText)
    (self.some, Seq())
  }

  override def draw(r: WindowRenderer): WindowRenderer = {
    val draws = text.zipWithIndex.map { case (t, i) =>
      t.draw(0, i) _
    }
    r <++| draws
  }
}
