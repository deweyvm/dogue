package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

class Button[T](selected:Text, unselected:Text, result:() => T, shouldActivate:() => Boolean, i:Int, j:Int) extends Menu[T] {
  lazy val lazyResult = result()

  def update = this

  def getResult:Option[T] = {
    shouldActivate().select(lazyResult.some, None)
  }

  def draw(r:WindowRenderer):WindowRenderer = {
    unselected.draw(i, j)(r)
  }

  def drawBg(r:WindowRenderer):WindowRenderer = {
    selected.draw(i, j)(r)
  }

}
