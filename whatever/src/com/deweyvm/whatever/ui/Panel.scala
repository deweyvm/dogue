package com.deweyvm.whatever.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.whatever.graphics.RectSprite
import com.deweyvm.gleany.graphics.Color

class Panel(val x:Int, val y:Int, val width:Int, val height:Int) {
  def contains(i:Int, j:Int):Boolean =
    i >= x && i < x + width && j >= y && j < y + height

  def getRect:Recti = Recti(x, y, width, height)
  def update():Panel = this
  def draw() {
    new RectSprite(width*16, height*16, Color.Blue).draw(x*16, y*16)
  }
}





