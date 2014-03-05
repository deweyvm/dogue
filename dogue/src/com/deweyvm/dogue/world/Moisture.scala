package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Array2dView, Array2d}
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.gleany.data.Point2d

class Moisture(cols:Int, rows:Int, height:Array2dView[Meters], wind:Array2dView[Arrow], speed:Double, steps:Int) {
  def followWind(w:Arrow, i:Double, j:Double):(Double,Double) = {
    (Point2d(i, j) - w.direction*speed).toTuple
  }

  //@tailrec
  private def traceWind(i:Double, j:Double, depthLeft:Int, current:Vector[Meters]):Vector[Meters] = {
    val w = wind.get(i.toInt, j.toInt)
    val (ni, nj) = followWind(w, i, j)
    if (ni < 0 || nj < 0 || ni > cols - 1 || nj > rows - 1) {
      return current
    }
    val h = height.get(ni.toInt, nj.toInt)
    //println("%d, %d => %s" format (ni, nj, h))
    val v = h +: current
    if (depthLeft <= 0) {
      v
    } else {
      //println(depthLeft)
      traceWind(ni, nj, depthLeft - 1, v)
    }
  }
  val oobHeight = 10000 m
  /**
   * depth required for the tile to count as a moisture producing tile
   */
  val moistureSpawnDepth = 0 m
  val map = Array2d.tabulate(cols, rows) {case (i, j) =>
    if (height.get(i, j) <= moistureSpawnDepth) {
      0
    } else {
      val path: Vector[Meters] = traceWind(i, j, steps, Vector())
      //path foreach {println(_)}
      val index = path.indexWhere( _ <= moistureSpawnDepth)
      if (index < 0) {
        0
      } else {
        index/steps.toDouble/2
      }
    }
  }
}
