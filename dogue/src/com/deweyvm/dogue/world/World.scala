package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d, Code}
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.data._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.gleany.data.Rectd
import com.deweyvm.dogue.common.Implicits
import Implicits._

case class WorldParams(period:Int, octaves:Int, size:Int, seed:Long) {
  val name = new MapName(seed).makeName
}

class World(val worldParams:WorldParams) {
  val (cols, rows) = (worldParams.size, worldParams.size)

  val maxElevation = 10000.0
  val solidElevation = 0
  val solidTier = solidElevation/maxElevation

  val border = math.min(cols, rows)/2 - 10
  val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender
  //val roughNoise = noise.cut(4096, 4096, dogue.common.id, 0)
  val windMap: Lazy2d[(Point2d, Arrow, Color)] = {
    VectorField.perlinWind(solidElevation, noise, cols, rows, 1, worldParams.seed).lazyVectors
  }

  private def perlinToHeight(t:Double) = {
    val tm = t
    tm*tm*tm*tm
  }

  val heightMap:Indexed2d[Double] = {
    noise.map({ case (i, j, p) =>
      val h = perlinToHeight(p)
      val x = (cols/2 - i).toDouble
      val y = (rows/2 - j).toDouble
      val dist = math.sqrt(x*x + y*y)
      val d1 = dist/(cols/2.toDouble)
      val inner = 0.8
      assert(dist > cols/2 || (d1 >= 0 && d1 <= 1.0), "bad at (%d, %d), %.2f" format (i, j, d1))
      val d = if (d1 > 0.92) {
        1
      } else if (d1 < inner) {
        h
      } else {
        val slope = (d1-inner)/(1 - inner)
        (slope + h).clamp(-1, 1)
      }
      d * 10000
      /*val c = if (xCenter *xCenter + yCenter*yCenter < border * border) {
        /*t.toFloat + */1.2*math.pow(d1, 6) //<--- only rise near the edge, not the whole way
      } else {
        10
      }
      println(c)
      c*maxElevation*/
    })
  }

  val latRegions:Indexed2d[LatitudinalRegion] = {
    val max = cols/2
    Lazy2d.tabulate(cols, rows){ case (i, j) =>
      val x = cols/2 - i
      val y = rows/2 - j
      val lat = (x*x + y*y).sqrt/max
      LatitudinalRegion.getRegion(lat)
    }
  }

  val regionMap:Indexed2d[Color] = {
    val hexSize = cols/150
    val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, worldParams.seed)
    println("%d %d" format(hexGrid.hexCols, hexGrid.hexRows))
    val colors = (0 until hexGrid.graph.nodes.length).map {_ => Color.randomHue()}
    val graph = hexGrid.graph
    val colorMap = (colors zip graph.nodes).map { case (color, poly) =>
      (poly.self, color)
    }.toMap
    Lazy2d.tabulate(cols, rows){ case (i, j) =>
      heightMap.get(i, j) match {
        case Some(d) if d > 0 =>
          hexGrid.pointInPoly(i, j) match {
            case Some(poly) => colorMap(poly)
            case None => Color.Black
          }
        case _ => Color.Black
      }
    }
  }

  def getLatitude(i:Int, j:Int):LatitudinalRegion = {
    latRegions.get(i, j).getOrElse(Polar)
  }

  var eTime = 0.0
  var rTime = 0.0
  var wTime = 0.0
  def getElevation(i:Int, j:Int):Double = {
    val (h, time) = Timer.timer(() => {
      heightMap.get(i, j).getOrElse(maxElevation)
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
    val elevation = getElevation(i, j)
    val elevationTier:Int = (elevation/(maxElevation/10)).toInt
    val region = getRegion(i, j)
    val arrow = getWind(i, j)
    val windDir = arrow.direction * arrow.magnitude
    val color =
      if (elevationTier <= solidTier) {
        Color.Blue.dim((1/(1 - math.abs(elevationTier - 2)/10f)).toFloat)
      } else if (elevationTier == 1) {
        Color.Yellow
      } else if (elevationTier == 2) {
        Color.Green
      } else if (elevationTier <= 5) {
        Color.DarkGreen
      } else if (elevationTier == 6) {
        Color.DarkGrey
      } else if (elevationTier == 7) {
        Color.Grey
      } else if (elevationTier == 8){
        Color.White.dim(1.3f)
      } else if (elevationTier == 9) {
        Color.White.dim(1.2f)
      } else {
        Color.White.dim(1.1f)
      }
    val lat = getLatitude(i, j)
    val tile = new Tile(Code.intToCode(elevationTier), color, Color.White)

    new WorldTile(elevation, region, lat, windDir, tile)
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


  /////////////////////////////////////
  def makeVoronoiRegion:Indexed2d[Color] = {
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

}


