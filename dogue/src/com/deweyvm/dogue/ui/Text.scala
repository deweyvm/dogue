package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import scala.collection.immutable.IndexedSeq
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

object Text {
  def create(bgColor:Color, fgColor:Color):Text = {
    fromString(bgColor, fgColor)("")
  }

  def fromString(bgColor:Color, fgColor:Color)(s:String):Text = {
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
    Text.fromString(bgColor, fgColor)(s)
  }

  def draw(i:Int, j:Int)(r:WindowRenderer):WindowRenderer = {
    r <+| filterDraw(i, j, {case (_:Int,_:Int) => true})
  }

  def filterDraw(i:Int, j:Int, f:(Int, Int) => Boolean)(r:WindowRenderer):WindowRenderer =  {
    val d = letters.zipWithIndex.map { case (tile, k) =>
      f(i + k, j).partial((i + k, j, tile))
    }.flatten
    r <++ d
  }
}

