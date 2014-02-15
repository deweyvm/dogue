package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti

class TitlePanel(override val rect:Recti,
                 bgColor:Color)
  extends Panel(rect, bgColor) {
  val title = new Text("Dogue", Color.Black, Color.White)
  override def draw() {
    super.draw()
    title.draw(width/2 - title.width/2, height/2)
  }

}
