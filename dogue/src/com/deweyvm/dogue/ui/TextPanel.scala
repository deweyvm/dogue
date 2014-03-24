package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

object TextPanel {
  def create(width:Int, bgColor:Color, fgColor:Color) = new TextPanel(width, bgColor, fgColor, Vector())
}

case class TextPanel(width:Int, bgColor:Color, fgColor:Color, text:Vector[Text]) extends WindowContents {
  override def update(s: Seq[WindowMessage]): (Option[TextPanel], Seq[Window]) = {
    val doClear = s.contains(Clear)
    val strings = s.map {
      case TextMessage(str) => str.toLines(width).map{ s => Text.fromString(s, bgColor, fgColor)}.some
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
