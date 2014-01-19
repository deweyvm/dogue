package com.deweyvm.whatever.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory

class TitlePanel(override val x:Int,
                 override val y:Int,
                 override val width:Int,
                 override val height:Int,
                 factory:GlyphFactory) extends Panel(x, y, width, height) {
  val title = new Text("Whorl", Color.Black, Color.White, factory)
  override def draw() {
    super.draw()

  }

}
