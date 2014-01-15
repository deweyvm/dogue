package com.deweyvm.whatever.world

import com.deweyvm.whatever.graphics.{GlyphFactory}
import com.deweyvm.whatever.{Game, Assets}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.ui.Text
import com.deweyvm.whatever.entities.{Tile, Code}
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.data.{Point2i, Recti}


class Stage {
  val cols = Game.RenderWidth/16
  val rows = Game.RenderHeight/16
  val factory = new GlyphFactory(16, 16, 16, 16, Assets.characterMap)
  val grid = new Grid(31, 16, 50, 50, factory)
  val rightPartition = 32
  val testText = new Text("this is a test", Color.Blue, Color.White, factory)
  val panels = ArrayBuffer[Panel]()
  val borders = ArrayBuffer[(Int, Int, Tile)]()

  def init() {
    panels += new Panel(1, 1, cols/2 - 1 - 1, rows - 8 - 1)
    panels += new Panel(cols/2, 1, cols/2 - 1, rows - 1 - 1)
    panels += new Panel(1, rows - 8 + 1, cols/2 - 1 - 1, 8 - 1 - 1)
    updateBorders()
  }

  def update() {
    grid.update()
  }

  def draw() {
    grid.draw(rightPartition, 1)
    borders foreach { case (i, j, tile) =>
      tile.draw(i, j)
    }
  }

  def updateBorders() {
    borders.clear()
    for (i <- 0 until (Game.RenderWidth/16);  j <- 0 until (Game.RenderHeight/16)) {
      def addBorder(code:Code): Unit = {
        borders += ((i, j, code.makeTile(Color.Black, Color.White, factory)))
      }
      if (isSolid(i, j)) {

      } else {
        val up = isSolid(i, j-1)
        val down = isSolid(i, j+1)
        val right = isSolid(i + 1, j)
        val left = isSolid(i - 1, j)
        val code = (up, down, left, right) match {
          case (true,  true,  _,     _)     => Code.═
          case (_,     _,     true,  true)  => Code.║
          case (true,  false, false, false) => Code.╦
          case (false, true,  false, false) => Code.╩
          case (false, false, true,  false) => Code.╠
          case (false, false, false, true)  => Code.╣
          case (false, true,  false, true)  => Code.╝
          case (true,  false, true,  false) => Code.╔
          case (true,  false, false, true)  => Code.╗
          case (false, true,  true,  false) => Code.╚
          case _                            => Code.?
        }
        addBorder(code)
      }
    }
  }



  def isSolid(i:Int, j:Int): Boolean = {
    if (i < 0 || i > cols - 1 || j < 0 || j > rows - 1) {
      true
    } else {
      panels exists { p =>
        p.contains(i, j)
      }
    }

  }

  init()
}


class Panel(val x:Int, val y:Int, val width:Int, val height:Int) {
  def contains(i:Int, j:Int):Boolean = {
    i >= x && i < x + width && j >= y && j < y + height
  }

  def drawText(text:Text, i:Int, j:Int) {
    text.letters.zipWithIndex map { case (tile, k) =>
      val ii = i + k
      if (contains(ii, j)) {
        tile.draw(i + k, j)
      }
    }
  }
}
