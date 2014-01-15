package com.deweyvm.whatever.entities

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory

//code page 437
object Code {
  case object ═ extends Code(205)
  case object ║ extends Code(186)
  case object ╣ extends Code(185)
  case object ╩ extends Code(202)
  case object ╦ extends Code(203)
  case object ╠ extends Code(204)
  case object ╗ extends Code(187)
  case object ╔ extends Code(201)
  case object ╝ extends Code(188)
  case object ╚ extends Code(200)
  case object ? extends Code('?'.toInt)
}

class Code(index:Int) {
  def makeTile(bgColor:Color, fgColor:Color, factory:GlyphFactory) = {
    new Tile(bgColor, fgColor, index, factory)
  }
}