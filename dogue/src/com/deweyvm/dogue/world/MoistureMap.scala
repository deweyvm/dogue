package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Array2dView, Array2d}
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.gleany.data.Point2d
import scala.annotation.tailrec
import java.util.Random
import com.deweyvm.dogue.DogueImplicits._
class MoistureMap(surface:SurfaceMap, latitude:Array2dView[Double], wind:Array2dView[Arrow], speed:Double, steps:Int, seed:Long) {
  val cols = latitude.cols
  val rows = latitude.rows
  val r = new Random(seed)
  val rp = r.nextDouble - 0.5
  def followWind(w:Arrow, i:Double, j:Double):(Double,Double) = {
    (Point2d(i, j) - w.direction.rotate(rp)*speed).toTuple
  }

  @tailrec
  private def traceWind(i:Double, j:Double, depthLeft:Int, current:Vector[Meters]):Vector[Meters] = {
    val w = wind.get(i.toInt, j.toInt)
    val (ni, nj) = followWind(w, i, j)
    if (ni < 0 || nj < 0 || ni > cols - 1 || nj > rows - 1) {
      return current
    }
    val t = surface.landMap.get(ni.toInt, nj.toInt)
    val h = surface.heightMap.get(ni.toInt, nj.toInt)
    val v = h +: current
    if (depthLeft <= 0 || t.isWater) {
      v
    } else {
      val newDepth = if (h > mountainHeight) {
        depthLeft - 2
      } else {
        depthLeft - 1
      }
      traceWind(ni, nj, newDepth, v)
    }
  }
  val mountainHeight = 2000 m
  /**
   * depth required for the tile to count as a moisture producing tile
   * fixme: use water body map issue #257
   */
  val moistureSpawnDepth = 0 m

  def get(i:Int, j:Int) = {
    val lat = latitude.get(i, j).clamp(0, 0.6)
    val alt = 1 - math.abs(surface.heightMap.get(i, j).d)/10000
    val latm1 = 1 - lat
    val max = latm1*10000*alt*alt
    val raw = map.get(i, j)

    (raw * (latm1*latm1)*max).`mm/yr`
  }

  def linearToMoisture(d:Double) = d*d

  private val map = Array2d.tabulate(cols, rows) {case (i, j) =>
    if (surface.landMap.get(i, j).isWater) {
      0
    } else {
      val path: Vector[Meters] = traceWind(i, j, steps, Vector())
      val index = path.indexWhere( _ <= moistureSpawnDepth)
      if (index < 0) {
        0
      } else {
        linearToMoisture(1 - path.length/steps.toDouble)
      }
    }
  }
}
