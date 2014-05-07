package com.deweyvm.dogue.graphics

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Text

case class ColorScheme(fg:Color,
                       bg:Color,
                       selectFg:Color,
                       selectBg:Color,
                       border:Color) {
  def makeText = Text.fromString(bg, fg) _
  def makeSelectedText = Text.fromString(selectBg, selectFg) _
  def makeBorderText = Text.fromString(bg, border) _
}
