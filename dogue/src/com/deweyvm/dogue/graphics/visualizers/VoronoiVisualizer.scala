package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.common.procgen.{Polygon, PoissonRng}
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.data.{Point2d, Rectd}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.OglRenderer

class VoronoiVisualizer {
  val vorSize = 300
  val vorScale = 30
  val vorSeed = 1L
  val size = vorSize
  val scale = vorScale
  val pts = new PoissonRng(size, size, {case (i, j) => scale}, scale, vorSeed).getPoints
  val edges = Voronoi.getEdges(pts, size, size)
  val polys = Voronoi.getFaces(edges, Rectd(0, 0, size, size)) map { p:Polygon =>
    val mapped = p.lines map { _.p }
    flattenVector(mapped)
  }
  val colors = polys map {_ => Color.randomHue()}

  private def flattenVector(pts:Vector[Point2d]):Array[Float] = {
    val flat = pts.foldRight(Vector[Float]()){ case (p, acc) =>
      p.x.toFloat +: (p.y.toFloat +: acc)
    }
    Array(flat:_*)
  }

  def render(r:OglRenderer) {

    r.camera.translate(-100,-30)
    r.shape.setProjectionMatrix(r.camera.getProjection)
    r.drawRect(0,0,size,size, Color.Black)
    edges foreach { e =>
      r.drawLine(e.vorStart, e.vorEnd, Color.White)
      //r.drawLine(e.triStart, e.triEnd, Color.Green)
      r.drawPoint(e.triStart, Color.Red)
      r.drawPoint(e.triEnd, Color.Red)
    }
    polys.zip(colors) foreach { case (p, c) => ()
      r.drawPolygon(p, c)
    }

    r.camera.translate(100,30)
  }
}
