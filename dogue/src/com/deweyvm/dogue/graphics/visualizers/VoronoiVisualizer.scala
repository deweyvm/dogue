package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.common.procgen.{PoissonRng, PerlinNoise, Polygon}
import com.deweyvm.dogue.common.procgen.voronoi.{VoronoiGraph, Voronoi}
import com.deweyvm.gleany.data.{Time, Point2d, Rectd}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.dogue.input.Controls

class VoronoiVisualizer {
  val vorSize = 500
  val vorScale = 25
  var vorSeed = 37L
  val size = vorSize
  val scale = vorScale

  def make = {
    val pts = new PoissonRng(size, size, {case (i, j) => scale}, scale, vorSeed).getPoints
    val edges = Time.printMillis(() => Voronoi.getEdges(pts, size, size, vorSeed))
    val faces = Time.printMillis(() => Voronoi.getFaces(edges, Rectd(0, 0, size, size)))
    val polys = faces map { p:Polygon =>
      val mapped = p.lines map { _.p }
      flattenVector(mapped.toVector)
    }
    Time.printMillis(() => new VoronoiGraph(faces))
    val colors = polys map {_ => Color.randomHue()}
    (edges, polys.zip(colors))
  }

  var (edges, polys) = make


  private def flattenVector(pts:Vector[Point2d]):Array[Float] = {
    val flat = pts.foldRight(Vector[Float]()){ case (p, acc) =>
      p.x.toFloat +: (p.y.toFloat +: acc)
    }
    Array(flat:_*)
  }

  def render(r:OglRenderer) {

    if (Controls.Space.justPressed) {
      vorSeed += 1
      val (e, p) = make
      edges = e
      polys = p
    }

    r.camera.translate(-100,-30)
    r.shape.setProjectionMatrix(r.camera.getProjection)
    r.drawRect(0,0,size,size, Color.Black)
    edges foreach { e =>
      r.drawLine(e.vorStart, e.vorEnd, Color.White)
      //r.drawLine(e.triStart, e.triEnd, Color.Green)
      r.drawPoint(e.triStart, Color.Red)
      r.drawPoint(e.triEnd, Color.Red)
    }
    polys foreach { case (p, c) => ()
      r.drawPolygon(p, c)
    }

    r.camera.translate(100,30)
  }
}
