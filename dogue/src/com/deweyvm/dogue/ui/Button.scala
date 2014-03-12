package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

class Button[T](t:Text, result:() => T, shouldActivate:() => Boolean, i:Int, j:Int) extends Menu[T] {
  lazy val lazyResult = result()

  def update = this

  def getResult:Option[T] = {
    shouldActivate().select(lazyResult.some, None)
  }

  def draw() {
    t.draw(i, j)
  }

  def drawBg() {
    t.setBg(Color.Black).draw(i, j)
  }

}
