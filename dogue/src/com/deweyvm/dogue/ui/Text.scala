package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.Dogue
import scala.collection.immutable.IndexedSeq

object Text {
  def create(bgColor:Color, fgColor:Color):Text = {
    fromString("", bgColor, fgColor)
  }

  def fromString(s:String, bgColor:Color, fgColor:Color):Text = {
    val letters = stringToLetters(s, bgColor, fgColor)

    new Text(letters, bgColor, fgColor)
  }

  def stringToLetters(s:String, bgColor:Color, fgColor:Color) = {
    s map { c =>
      Tile(Code.unicodeToCode(c), bgColor, fgColor)
    }
  }

}

class Text(letters:IndexedSeq[Tile], bgColor:Color, fgColor:Color) {
  def width = letters.length

  def append(s:String):Text = {
    val newLetters = letters ++ Text.stringToLetters(s, bgColor, fgColor)
    new Text(newLetters, bgColor, fgColor)
  }

  def setBg(color:Color):Text = {
    val newLetters = letters map {_.copy(bgColor = color)}
    new Text(newLetters, color, fgColor)
  }

  def setString(s:String):Text = {
    Text.fromString(s, bgColor, fgColor)
  }

  def draw(i:Int, j:Int) {
    filterDraw(i, j, {case (_:Int,_:Int) => true})
  }

  def filterDraw(i:Int, j:Int, f:(Int, Int) => Boolean) {
    letters.zipWithIndex foreach { case (tile, k) =>
      if (f(i + k, j)) {
        tile.draw(i + k, j)
      }
    }
  }
}

