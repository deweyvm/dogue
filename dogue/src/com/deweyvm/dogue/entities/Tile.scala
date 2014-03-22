package com.deweyvm.dogue.entities

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.Dogue

object Tile {
  val Blank = Tile(Code.`?`, Color.White, Color.Pink)
}
case class Tile(code:Code, bgColor:Color, fgColor:Color) {
  /*def draw(i:Int, j:Int) = {
    Dogue.renderer.draw(this, i, j)
  }*/

}
