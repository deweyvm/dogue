package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen.{Line, Polygon}
import com.deweyvm.gleany.data.{Rectd, Point2d}
import scala.util.Random
import com.deweyvm.dogue.common.data.{Lazy2d, Angles}
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._

object PlateTranslator {
  def create(r:Random, size:Int, timescale:Int) = {
    val k = r.nextInt(7) + 1
    val period = 180//timescale + (timescale/4) * r.nextDouble()
    val radius = 150 + 30*r.nextDouble()
    val t0 = r.nextInt() % period
    def translate(t:Long) = {
      import math.{sin, cos}
      val a = (t + t0)*Angles.Tau/period
      val x = radius*(k*sin(a)*cos(k*a) + cos(a)*sin(k*a))//radius * f(k*a) * g(a)
      val y = radius*(k*cos(a)*cos(k*a) - sin(a)*sin(k*a))//radius * f(k*a) * f(a)
      Point2d(x, y)
    }
    new PlateTranslator(translate)
  }
}

class PlateTranslator(f:Long => Point2d) {
  def apply(t:Long) = f(t)
}

case class Plate(original:Polygon, poly:Polygon, color:Color, m:PlateTranslator) {
  val diff = (poly.centroid - original.centroid).magnitude
  def update(t:Long):Plate = copy(poly = poly.translate(m(t)))
}

object TectonicPlates {
  def create(size:Int, seed:Long):TectonicPlates = {
    val r = new Random(seed + 1)
    val numPlates = 50
    val buff = size/2
    val tecSize = size*2 + buff*2
    val tectonicPoints = (0 until numPlates) map { _ =>
      Point2d(r.nextDouble*tecSize - buff, r.nextDouble*tecSize - buff)
    }
    val edges = Voronoi.getEdges(tectonicPoints, tecSize, tecSize, seed)
    val faces = Voronoi.getFaces(edges, Rectd(-Int.MaxValue/10, -Int.MaxValue/10, Int.MaxValue, Int.MaxValue))
    val plates = faces map { face =>
      new Plate(face, face, Color.randomHue(), PlateTranslator.create(r, size, 1))
    }
    TectonicPlates(plates)
  }
}

case class TectonicPlates private (plates:Vector[Plate]) {
  def update(t:Long):TectonicPlates = TectonicPlates(plates map {_.update(t)})
}
