package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color

case class InputPanel(override val x:Int,
                      override val y:Int,
                      override val width:Int,
                      override val height:Int,
                      bgColor:Color,
                      factory:GlyphFactory,
                      textInput:TextInput)
  extends Panel(x, y, width, height, bgColor) {

  override def update = {
    this.copy(textInput = textInput.update)
  }

  override def draw() {
    super.draw()
    textInput.draw(x, y)
  }

}
