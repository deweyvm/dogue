package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.procgen.{Graph, Polygon, PolygonUtils}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
import scala.util.Random
import com.deweyvm.dogue.input.Controls
import scala.collection.immutable.IndexedSeq

class HexGridVisualizer {
  val cols = 50
  val rows = 50 - 1
  var seed = 0
  val hexCols = cols - 1
  val hexRows = (rows - 1)/2
  def makeHexes = {
    val size = 10.0
    val wiggleSize = size
    val factor = math.sqrt(3)/6
    val r = new Random(seed)
    def rd = (r.nextDouble() - 0.5)*wiggleSize
    Array2d.tabulate(cols, rows) { case (i, j) =>

      val sign = (i.isOdd == j.isOdd).select(1, -1)
      Point2d(i*1.5*size + sign*size*factor, j*size)// + rd.dup
    }
  }

  def indexToCoords(i:Int) = {
    (i % (cols - 1), (i / (cols - 1)) * 2)
  }

  def coordsToIndex(i:Int, j:Int) = {
    i + (j/2)*(cols - 1)
  }

  def makePolys:IndexedSeq[Polygon] = {
    val polys = for (i <- 0 until hexCols*hexRows) yield {
      val (x0, y0) = indexToCoords(i)
      //val x0 = i % (cols - 1)
      //val y0 = (i / (cols - 1)) * 2
      val yOffset = x0.isOdd select (1, 0)
      val x = x0
      val y = y0 + yOffset
      for {
        UL <- hexes.get(x,     y)
        UR <- hexes.get(x + 1, y)
        L  <- hexes.get(x,     y + 1)
        R  <- hexes.get(x + 1, y + 1)
        LL <- hexes.get(x,     y + 2)
        LR <- hexes.get(x + 1, y + 2)
      } yield {
        Polygon.fromPoints(Vector(UL, UR, R, LR, LL, L))
      }
    }
    polys.flatten
  }

  def makeGraph(){}/*:Graph[Polygon, Vector] = {
    for (i <- 0 until (cols - 1)*((rows - 1)/2)) yield {
      val current = polys(i)
      val x0 = i % (cols - 1)
      val y0 = (i / (cols - 1)) * 2
    }
  }*/

  var hexes = makeHexes
  var polys = makePolys
  var graph = makeGraph

  def render(r:OglRenderer) {
    if (Controls.Space.justPressed) {
      seed += 1
      hexes = makeHexes
      polys = makePolys
    }
    r.translateShape(100,30) {() =>
      var color = 0.1f

      hexes foreach { case (i, j, h) => ()
        r.drawPoint(h, Color.White, 1)
      }
      polys foreach {poly =>
        val c = Color.fromHsb(color % 1)
        color += 0.1f
        r.drawPolygon(poly, c)
      }
    }


  }
}
