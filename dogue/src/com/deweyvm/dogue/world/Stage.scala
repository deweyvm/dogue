package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui._
import com.deweyvm.dogue.entities.Tile

import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.data.{Code, Array2d}
import com.deweyvm.dogue.graphics.{OglRenderer, Renderer}
import com.deweyvm.gleany.data.{Point2d, Point2f, Recti}
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.Dogue

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

case class Stage(cols:Int, rows:Int, panels:Vector[Panel], serverStatus:Text) {
  val rect = Recti(0, 0, cols, rows)

  val rightPartition = 32
  val testText = new Text("this is a test", Color.Blue, Color.White)
  val borders = calculateBorders

  def update:Stage = {
    this.copy(panels = panels map (_.update),
              serverStatus = serverStatus.setString(Client.instance.getStatus))
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
        Tile(code, Color.Black, Color.White).some
      }
    }
  }

  def isSolid(i:Int, j:Int): Boolean = {
    !rect.contains(Point2d(i,j)) || (panels exists { p =>
      p.contains(i, j)
    })
  }

}


