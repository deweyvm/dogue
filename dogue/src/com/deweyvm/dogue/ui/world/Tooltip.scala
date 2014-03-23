package com.deweyvm.dogue.ui.world

import com.deweyvm.gleany.graphics.Color

case class Tooltip(color:Color, lines:Vector[String]) {
  def append(lines:Vector[String]) = copy(lines = this.lines ++ lines)
  def prepend(lines:Vector[String]) = copy(lines = lines ++ this.lines)
}
