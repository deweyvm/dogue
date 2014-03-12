package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.common.procgen.{PolygonUtils, PerlinNoise, PoissonRng, Polygon}
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.data.{Timer, Rectd}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.dogue.input.Controls

class VoronoiVisualizer extends Visualizer {
  val vorSize = 500
  val vorScale = vorSize/15.0
  var vorSeed = 37L
  val size = vorSize
  val scale = vorScale

  def time[T](s:String, f: () => T) = {
    val (t, time) = Timer.timer(f)
    println("%s: %dms" format (s, time/1000000))
    t
  }

  def make = {
    val perlin = time("Perlin noise: ", () => new PerlinNoise(1/128.0, 5, size, vorSeed).render)
    val pts = time("Generate points: ", () => {
      new PoissonRng(size, size, {case (i, j) => scale}, scale, vorSeed).getPoints.filter { pt =>
        perlin.get(pt.x.toInt, pt.y.toInt) match {
          case Some(d) => d > 0.2
          case None => true
        }
        true
      }
    })

    val edges = time("Generate Voronoi: ", () => Voronoi.getEdges(pts, size, size, vorSeed))
    val faces = time("Getting Faces:", () => Voronoi.getFaces(edges, Rectd(0, 0, size, size)))
    val polys = time("Flatten polys:", () => faces map { p:Polygon =>
      val mapped = p.lines map { _.p }
      PolygonUtils.flattenVector(mapped.toVector)
    })

    val colors = polys map {_ => Color.randomHue()}
    (edges, polys.zip(colors))
  }

  var (edges, polys) = make



  override def drawBatch(r:OglRenderer) {

    if (Controls.Space.justPressed) {
      vorSeed += 1
      val (e, p) = make
      edges = e
      polys = p
    }

    r.translateShape(100,30) {() =>
      r.drawRect(0,0,size,size, Color.Black)
      edges foreach { e =>
        r.drawLine(e.vorStart, e.vorEnd, Color.White)
        //r.drawLine(e.triStart, e.triEnd, Color.Green)
        r.drawPoint(e.triStart, Color.Red)
        r.drawPoint(e.triEnd, Color.Red)
      }
      polys foreach { case (p, c) => ()
        //r.drawPolygon(p, c)
      }
    }


  }
}
