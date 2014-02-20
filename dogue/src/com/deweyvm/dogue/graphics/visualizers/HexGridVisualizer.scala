package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.dogue.common.data.{Code, Array2d}
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.procgen.{HexGrid, Node, Graph, Polygon}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
import scala.util.Random
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.entities.Tile



class HexGridVisualizer {
  val cols = 49
  val rows = 49
  var seed = 0

  var hexGrid = makeHexGrid

  def makeHexGrid = new HexGrid(10, cols, rows, 0, seed)

  def batchDraw(r:OglRenderer) {
    hexGrid.graph.nodes.foreach { node =>
      val poly = node.self
      val upLeft = poly.upperLeft
      upLeft foreach { pt =>
        val c = getColor(node.getNeighbors.length)
        val code = Code.unicodeToCode((node.getNeighbors.length + 48).toChar)
        val tile = new Tile(code, Color.Blank, c)
        //r.drawTileRaw(tile, pt.x + 100 - 3, pt.y + 30 + 2)
      }
    }
  }


  def render(r:OglRenderer) {
    if (Controls.Space.justPressed) {
      seed += 1
      hexGrid = makeHexGrid
    }
    r.translateShape(100,30) {() =>
      hexGrid.hexes foreach { case (i, j, h) => ()
        r.drawPoint(h, Color.White, 1)
      }
      hexGrid.graph.nodes.foreach { node =>
        val poly = node.self
        val c = getColor(node.getNeighbors.length)
        r.drawPolygon(poly, c)
      }
    }


  }

  def getColor(i:Int) = i match {
    case 1 => Color.Blue
    case 2 => Color.Green
    case 3 => Color.Yellow
    case 4 => Color.Orange
    case 5 => Color.Red
    case 6 => Color.Purple
    case _ => Color.White
  }
}
