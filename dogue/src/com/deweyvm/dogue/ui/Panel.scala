package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.{Point2f, Recti}
import com.deweyvm.dogue.graphics.{Renderer, RectSprite}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.Dogue

class Panel(val x:Int, val y:Int, val width:Int, val height:Int, bgColor:Color) {
  def contains(i:Int, j:Int):Boolean =
    getRects exists { _.contains(Point2f(i,j)) }

  def getRects:Vector[Recti] = Vector(Recti(x, y, width, height))
  def update:Panel = this
  def draw() {
    for (i <- x until (x + width);
         j <- y until (y + height)) {
      new Tile(Code.` `, bgColor, bgColor).draw(i, j)
    }


    //new RectSprite(width*16, height*16, bgColor).draw(x*16, y*16)
  }
}





