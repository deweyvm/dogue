package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.procgen.PolygonUtils
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
class HexGridVisualizer {
  val cols = 50
  val rows = 50 - 1
  val hexes = Array2d.tabulate(cols, rows) { case (i, j) =>
    val size = 10.0
    val factor = 4/(math.sqrt(3)/2)
    val xOffset:Double = (j.isOdd, i.isOdd) match {
      case (true, true) => size/factor
      case (true, false) => -size/factor
      case (false, true) => -size/factor
      case (false, false) => size/factor
    }
    Point2d(i*size + xOffset, j*size)
  }
  val polys: Seq[Option[Array[Float]]] = for (i <- 0 until (cols - 1)*((rows - 1)/2)) yield {

    val x0 = i % (cols - 1)
    val y0 = (i / (cols - 1)) * 2
    val (xOffset:Int, yOffset:Int) = (y0.isOdd, x0.isOdd) match {
      case (true, true) => (0, 1)
      case (true, false) => (0, 0)
      case (false, true) => (0, 1)
      case (false, false) => (0,0)
    }
    val x = x0
    val y = y0 + yOffset
    for {
      UL <- hexes.get(x, y)
      UR <- hexes.get(x + 1, y)
      L <- hexes.get(x, y + 1)
      R <- hexes.get(x + 1, y + 1)
      LL <- hexes.get(x, y + 2)
      LR <- hexes.get(x + 1, y + 2)
    } yield {
      PolygonUtils.flattenVector(Vector(UL, UR, R, LR, LL, L, UL))
    }
  }
  def render(r:OglRenderer) {
    r.camera.translate(-100,-30)
    r.shape.setProjectionMatrix(r.camera.getProjection)

    var color = 0.1f

    hexes foreach { case (i, j, h) =>
      r.drawPoint(h, Color.White)
    }
    polys foreach {poly =>
      poly foreach { p =>
        r.drawPolygon(p, Color.fromHsb(color % 1))
        color += 0.1f
      }
    }


    r.camera.translate(100,30)
  }
}
