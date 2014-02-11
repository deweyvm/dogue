package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Indexed2d, Code}
import com.deweyvm.dogue.common.procgen.PerlinNoise

import scala.math._
import com.deweyvm.dogue.entities.Tile


object WorldParams {
  def default = WorldParams(64, 6, 1024)
}
case class WorldParams(period:Int, octaves:Int, size:Int, seed:Int=0)

class World(val worldParams:WorldParams) {
  val (cols, rows) = (worldParams.size, worldParams.size)
  val (iSpawn, jSpawn) = (0,0)

  val border = min(cols, rows)/2 - 10

  val tiles:Indexed2d[WorldTile] = {
    import scala.math._
    val noise = new PerlinNoise(1/worldParams.period.toFloat, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender
    noise.cut[Double](cols, rows, x => x, 0).map({ case (i, j, t) =>

      val xCenter = abs(cols/2 - i)
      val yCenter = abs(cols/2 - j)
      val dist = sqrt(xCenter *xCenter + yCenter*yCenter)
      val d1 = dist/border
      val c = if (xCenter *xCenter + yCenter*yCenter < border * border) {
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
      } else if (region == 8){
        Color.White.dim(1.2f)
      } else if (region == 9) {
        Color.White.dim(1.1f)
      } else {
        Color.White
      }
      WorldTile(c, c, new Tile(Code.intToCode((c*10 + 48 + 4).toInt), color, Color.White))
    })
  }

  def update:World = this

}


