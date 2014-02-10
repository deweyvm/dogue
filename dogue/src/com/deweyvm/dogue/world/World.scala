package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d, Code}
import com.deweyvm.dogue.common.procgen.PerlinNoise

import com.deweyvm.dogue.entities.Tile


object WorldParams {
  def default = WorldParams(64, 6, 1024)
}
case class WorldParams(period:Int, octaves:Int, size:Int, seed:Int=0)

class World(val worldParams:WorldParams) {
  val (cols, rows) = (worldParams.size, worldParams.size)
  val (iSpawn, jSpawn) = (0,0)

  val regions = {
    val dummyRegion = new Region(16,16)
    def getRegion(i:Int, j:Int) = dummyRegion
    new Lazy2d(getRegion, cols, rows)
  }

  val tiles:Indexed2d[WorldTile] = {
    import scala.math._
    val noise = new PerlinNoise(1/worldParams.period.toFloat, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender
    val diagonal = min(cols, rows)/2
    noise.cut[Double](cols, rows, x => x, 0).map({ case (i, j, t) =>

      val xCenter = abs(cols/2 - i)
      val yCenter = abs(cols/2 - j)
      val dist = sqrt(xCenter *xCenter + yCenter*yCenter)
      val d1 = dist/diagonal
      val c = if (xCenter *xCenter + yCenter*yCenter < diagonal * diagonal) {
        t.toFloat + 1.2*pow(d1, 6)
      } else {
        10
      }
      val region = (c*10).toInt
      val color = if (region <= 0) {
        Color.Blue.dim(1/(1 - abs(region - 2)/10f))
      } else if (region == 1) {
        Color.Yellow
      } else if (region == 2) {
        Color.Green
      } else if (region <= 5) {
        Color.DarkGreen
      } else if (region == 6) {
        Color.DarkGrey
      } else if (region == 7) {
        Color.Grey
      } else if (region > 7){
        Color.White
      } else {
        throw new Exception("impossible " + region)
      }
      WorldTile(c, new Tile(Code.intToCode((c*10 + 48 + 4).toInt), color, Color.White))
    })
  }

  def update:World = this

}


