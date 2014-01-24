package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.GlyphFactory

class TitlePanel(override val x:Int,
                 override val y:Int,
                 override val width:Int,
                 override val height:Int,
                 bgColor:Color,
                 factory:GlyphFactory) extends Panel(x, y, width, height, bgColor) {
  val title = new Text("Dogue", Color.Black, Color.White, factory)
  override def draw() {
    super.draw()
    title.draw(width/2 - title.width/2, height/2)
  }

}
