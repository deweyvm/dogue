package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d, Code}
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.graphics.OglRenderer._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.data.Rectd


case class WorldParams(period:Int, octaves:Int, size:Int, seed:Long) {
  val name = new MapName(seed).makeName
}

class World(val worldParams:WorldParams) {
  val (cols, rows) = (worldParams.size, worldParams.size)

  val border = math.min(cols, rows)/2 - 10
  val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender

  val windMap: Lazy2d[(Point2d, Arrow, Color)] = {
    VectorField.perlinWind(noise, cols, rows, 1, worldParams.seed).lazyVectors
    /*Lazy2d.tabulate(cols, rows) {case (i, j) =>
      (Point2d.UnitX, Arrow(Point2d.UnitX, 1), Color.Black)
    }*/
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
    val regionSize = cols/8
    val regionCenters = new PoissonRng(size, size, {case (i, j) => regionSize}, regionSize, vorSeed).getPoints
    val edges = Voronoi.getEdges(regionCenters, size, size)
    val faces = Voronoi.getFaces(edges, Rectd(0, 0, size, size))
    val colors = (0 until faces.length) map {_ => Color.randomHue()}
    val f = colors zip faces
    Lazy2d.tabulate(cols, rows){ case (i, j) =>
      f.find{case (color, poly) => poly.contains(Point2d(i, j))} map {_._1} getOrElse Color.Black
    }
  }


  def worldTiles:Indexed2d[WorldTile] = Lazy2d.tabulate(cols, rows){ case (i, j) =>
    val elevation:Int = heightMap.get(i, j).getOrElse(10)
    val region = regionMap.get(i, j).getOrElse(Color.Black)
    val (_, arr, _) = windMap.get(i, j).getOrElse((Point2d(0,0), Arrow(Point2d.UnitX, 1), Color.Black))
    val windDir = arr.direction * arr.magnitude
    val color =
      if (elevation <= 0) {
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
        Color.White.dim(1.2f)
      } else if (elevation == 9) {
        Color.White.dim(1.1f)
      } else {
        Color.White
      }
    new WorldTile(elevation, elevation, region, windDir, new Tile(Code.` `, color, Color.White))
  }

  def update:World = this

}


