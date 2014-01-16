package com.deweyvm.whatever.world

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.ui.{WorldPanel, TextPanel, Panel, Text}
import com.deweyvm.whatever.entities.{Tile, Code}
import com.deweyvm.whatever.data.Array2d
import com.deweyvm.gleany.data.{Point2f, Recti}


object Stage {
  def create(factory:GlyphFactory, cols:Int, rows:Int):Stage = {
    val controlsHeight = 8
    val messagePanel = TextPanel.makeNew(1, 1, cols/2 - 1 - 1, rows - 8 - 1, factory)
                                .addText("line0", Color.White, Color.Black)
                                .addText("line1 should stretch onto multiple lines", Color.White, Color.Black)
                                .addText("line2", Color.White, Color.Black)
                                .addText("line3", Color.White, Color.Black)
                                .addText("line4", Color.White, Color.Black)
                                .addText("line5", Color.White, Color.Black)
                                .addText("line6", Color.White, Color.Black)
                                .addText("line7", Color.White, Color.Black)
                                .addText("line8", Color.White, Color.Black)
                                .addText("line9", Color.White, Color.Black)
                                .addText("line10", Color.White, Color.Black)
                                .addText("line11", Color.White, Color.Black)
                                .addText("line12", Color.White, Color.Black)
    val worldPanel = WorldPanel.create(0, 0, cols/2, 1, cols/2 - 1, rows - 1 - 1, 50, 50, factory)
    val controlPanel = new Panel(1, rows - controlsHeight + 1, cols/2 - 1 - 1, controlsHeight - 1 - 1)
    new Stage(cols, rows, factory, Vector(messagePanel, worldPanel, controlPanel))
  }
}

class Stage(cols:Int, rows:Int, factory:GlyphFactory, panels:Vector[Panel]) {
  val rect = Recti(0, 0, cols, rows)

  val rightPartition = 32
  val testText = new Text("this is a test", Color.Blue, Color.White, factory)
  val borders = calculateBorders

  def update:Stage = new Stage(cols, rows, factory, panels map (_.update))

  def draw() {
    panels foreach {_.draw()}
    borders foreach { case (i, j, tile) =>
      tile foreach { _.draw(i, j) }
    }
  }

  def calculateBorders:Array2d[Option[Tile]] = {
    Array2d.tabulate(cols, rows) { case (i,j) =>
      if (isSolid(i, j)) {
        None
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
        Some(code.makeTile(Color.Black, Color.White, factory))
      }
    }
  }

  def isSolid(i:Int, j:Int): Boolean = {
    !rect.contains(Point2f(i,j)) || (panels exists { p =>
      p.contains(i, j)
    })
  }

}


