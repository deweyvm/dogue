package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d, Code}
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.data._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.gleany.data.Rectd


case class WorldParams(period:Int, octaves:Int, size:Int, seed:Long) {
  val name = new MapName(seed).makeName
}

class World(val worldParams:WorldParams) {
  val (cols, rows) = (worldParams.size, worldParams.size)
  val solidElevation = 0
  val border = math.min(cols, rows)/2 - 10
  val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender
  //val roughNoise = noise.cut(4096, 4096, dogue.common.id, 0)
  val windMap: Lazy2d[(Point2d, Arrow, Color)] = {
    VectorField.perlinWind(solidElevation, noise, cols, rows, 1, worldParams.seed).lazyVectors
  }

  val heightMap:Indexed2d[Int] = {
    noise.map({ case (i, j, t) =>
      val xCenter = math.abs(cols/2 - i)
      val yCenter = math.abs(cols/2 - j)
      val dist = math.sqrt(xCenter *xCenter + yCenter*yCenter)
      val d1 = dist/border
      val c = if (xCenter *xCenter + yCenter*yCenter < border * border) {
        t.toFloat + 1.2*math.pow(d1, 6)
      } else {
        10
      }
      (c*10).toInt
    })
  }

  val regionMap:Indexed2d[Color] = {
    val size = cols
    val regionSize = size/10.0
    val regionCenters = new PoissonRng(size, size, { case (i, j) => regionSize}, regionSize, worldParams.seed).getPoints.filter{ pt =>
      heightMap.get(pt.x.toInt, pt.y.toInt) match {
        case Some(d) => d > 0
        case None => true
      }
    }

    val edges = Voronoi.getEdges(regionCenters, size, size, worldParams.seed)
    val faces = Voronoi.getFaces(edges, Rectd(0, 0,size,size))
    val colors = (0 until faces.length) map {_ => Color.randomHue()}
    val f = colors zip faces
    Lazy2d.tabulate(cols, rows){ case (i, j) =>
      heightMap.get(i, j) match {
        case Some(d) if d > 0 =>
          f.find{case (color, poly) => poly.contains(Point2d(i, j))} map {_._1} getOrElse Color.Black
        case _ => Color.Black
      }
    }
  }

  var eTime = 0.0
  var rTime = 0.0
  var wTime = 0.0
  def getElevation(i:Int, j:Int):Int = {
    val (h, time) = Timer.timer(() => {
      heightMap.get(i, j).getOrElse(10)
    })
    eTime += time
    h
  }
  def getRegion(i:Int, j:Int):Color = {
    val (r, time) = Timer.timer(() => {
      regionMap.get(i, j).getOrElse(Color.Black)
    })
    rTime += time
    r
  }
  def getWind(i:Int, j:Int):Arrow = {
    val (w, time) = Timer.timer(() => {
      windMap.get(i, j).getOrElse((Point2d(0,0), Arrow(Point2d.UnitX, 1), Color.Black)) match {
        case (_, arr, _) => arr
      }
    })
    wTime += time
    w
  }
  def worldTiles:Indexed2d[WorldTile] = Lazy2d.tabulate(cols, rows){ case (i, j) =>
    val elevation:Int = getElevation(i, j)
    val region = getRegion(i, j)
    val arrow = getWind(i, j)
    val windDir = arrow.direction * arrow.magnitude
    val color =
      if (elevation <= solidElevation) {
        Color.Blue.dim((1/(1 - math.abs(elevation - 2)/10f)).toFloat)
      } else if (elevation == 1) {
        Color.Yellow
      } else if (elevation == 2) {
        Color.Green
      } else if (elevation <= 5) {
        Color.DarkGreen
      } else if (elevation == 6) {
        Color.DarkGrey
      } else if (elevation == 7) {
        Color.Grey
      } else if (elevation == 8){
        Color.White.dim(1.3f)
      } else if (elevation == 9) {
        Color.White.dim(1.2f)
      } else {
        Color.White.dim(1.1f)
      }
    val tile = new Tile(Code.intToCode(elevation), color, Color.White)

    new WorldTile(elevation, elevation, region, windDir, tile)
  }

  def getTimeString = {

    val totalTime = rTime + eTime + wTime
    val r = (rTime/totalTime * 100).toInt
    val h = (eTime/totalTime * 100).toInt
    val w = (wTime/totalTime * 100).toInt
    "wind(%d) height(%d) region(%d)" format (w, h, r)
  }

  def update:World = {
    this
  }

}


