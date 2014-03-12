package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.{Point2d, Recti}
import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.world.Stage

class Panel(val rect:Recti, bgColor:Color) {
  val x = rect.x
  val y = rect.y
  val width = rect.width
  val height = rect.height
  def contains(i:Int, j:Int):Boolean =
    getRects exists { _.contains(Point2d(i,j)) }

  def getRects:Vector[Recti] = Vector(rect)
  def requestStage:Option[Stage] = None
  def update:Panel = this
  def draw() {
    getRects foreach {rect =>
      val xMin = rect.x
      val xMax = rect.x + rect.width
      val yMin = rect.y
      val yMax = rect.y + rect.height
      for (i <- xMin until xMax;
           j <- yMin until yMax) {
        new Tile(Code.` `, bgColor, bgColor).draw(i, j)
      }
    }
  }
}





