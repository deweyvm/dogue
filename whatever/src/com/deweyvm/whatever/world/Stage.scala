package com.deweyvm.whatever.world

import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.ui._
import com.deweyvm.whatever.entities.{Tile, Code}
import com.deweyvm.whatever.data.Array2d
import com.deweyvm.gleany.data.{Point2f, Recti}
import scala.Some
import com.deweyvm.whatever.Game
import com.deweyvm.whatever.input.Controls
import com.deweyvm.whatever.net.Client

object Stage {
  case object Title extends StageType {
    override def next = Chat
  }
  case object Chat extends StageType {
    override def next = World
  }
  case object World extends StageType {
    override def next = Title
  }
}

case class Stage(t:StageType, cols:Int, rows:Int, factory:GlyphFactory, panels:Vector[Panel], serverStatus:Text) {
  val rect = Recti(0, 0, cols, rows)

  val rightPartition = 32
  val testText = new Text("this is a test", Color.Blue, Color.White, factory)
  val borders = calculateBorders

  def update(stageFactory:StageFactory):Stage = {
    if (Controls.Tab.justPressed) {
      stageFactory.create(t.next)
    } else {
      this.copy(panels = panels map (_.update),
        serverStatus = serverStatus.setString(Client.instance.getString))
    }
  }

  def draw() {
    panels foreach { _.draw() }
    borders foreach { case (i, j, tile) =>
      tile foreach { _.draw(i, j) }
    }
    serverStatus.draw(cols - serverStatus.width, rows - 1)
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


