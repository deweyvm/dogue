package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

class Button[T](t:Text, result:() => T, shouldActivate:() => Boolean, i:Int, j:Int) extends Menu[T] {
  lazy val lazyResult = result()

  def update = this

  def getResult:Option[T] = {
    shouldActivate().select(lazyResult.some, None)
  }

  def draw(r:WindowRenderer):WindowRenderer = {
    t.draw(i, j)(r)
  }

  def drawBg(r:WindowRenderer):WindowRenderer = {
    t.setBg(Color.Black).draw(i, j)(r)
  }

}
