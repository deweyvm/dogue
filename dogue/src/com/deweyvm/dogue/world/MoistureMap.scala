package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Array2dView, Array2d}
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.gleany.data.Point2d
import scala.annotation.tailrec
import java.util.Random

class MoistureMap(cols:Int, rows:Int, height:Array2dView[(SurfaceType, Meters)], latitude:Array2dView[Double], wind:Array2dView[Arrow], speed:Double, steps:Int, r:Random) {

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
    val (t,h) = height.get(ni.toInt, nj.toInt)
    val v = h +: current
    if (depthLeft <= 0 || t.isWater) {
      v
    } else {
      val newDepth = if (h > mountainHeight) {
        depthLeft - 20
      } else {
        depthLeft - 1
      }
      traceWind(ni, nj, newDepth, v)
    }
  }
  val mountainHeight = 5000 m
  /**
   * depth required for the tile to count as a moisture producing tile
   * fixme: use water body map issue #257
   */
  val moistureSpawnDepth = 0 m

  def get(i:Int, j:Int) = {
    val lat = latitude.get(i, j).clamp(0, 0.6)
    val latm1 = 1 - lat
    val max = (1 - lat)*10000
    val raw = map.view.get(i, j)

    (raw * (latm1*latm1)*max).`mm/yr`
  }

  def linearToMoisture(d:Double) = d*d

  private val map = Array2d.tabulate(cols, rows) {case (i, j) =>
    if (height.get(i, j)._1.isWater) {
      0
    } else {
      val path: Vector[Meters] = traceWind(i, j, steps, Vector())
      //path foreach {println(_)}
      val index = path.indexWhere( _ <= moistureSpawnDepth)
      if (index < 0) {
        0
      } else {
        linearToMoisture(1 - path.length/steps.toDouble)
      }
    }
  }
}
