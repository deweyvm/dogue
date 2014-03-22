package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

case class TitleScreen(width:Int, height:Int, menu:Menu[Window]) extends WindowContents {
  val title = Text.fromString("Dogue", Color.Black, Color.White)
  def outgoing: Map[WindowId,Seq[WindowMessage]] = Map()
  def spawnWindow: Option[Window] = menu.getResult
  def update(s: Seq[WindowMessage]) = copy(menu = menu.update).some
  def draw() {
    title.draw(width/2 - title.width/2, height/2)
    menu.draw()
  }
}
