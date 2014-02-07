package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color

class TitlePanel(override val x:Int,
                 override val y:Int,
                 override val width:Int,
                 override val height:Int,
                 bgColor:Color) extends Panel(x, y, width, height, bgColor) {
  val title = new Text("Dogue", Color.Black, Color.White)
  override def draw() {
    super.draw()
    title.draw(width/2 - title.width/2, height/2)
  }

}
