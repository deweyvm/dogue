package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d}
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.Implicits
import Implicits._
import scala.annotation.tailrec
import com.deweyvm.gleany.data.{Point2d, Point2i}

class Moisture(cols:Int, rows:Int, height:Indexed2d[Meters], wind:Lazy2d[Arrow], speed:Double, steps:Int) {
  def followWind(w:Arrow, i:Double, j:Double):(Double,Double) = {
    (Point2d(i, j) - w.direction*speed).toTuple
  }

  //@tailrec
  private def traceWind(i:Double, j:Double, depthLeft:Int, current:Vector[Meters]):Vector[Meters] = {
    val v = for {
      w <- wind.get(i.toInt, j.toInt)
      (ni, nj) = followWind(w, i, j)
      h <- height.get(ni.toInt, nj.toInt)
    } yield {
      //println("%d, %d => %s" format (ni, nj, h))
      val v = h +: current
      if (depthLeft <= 0) {
        v
      } else {
        //println(depthLeft)
        traceWind(ni, nj, depthLeft - 1, v)
      }
    }
    val result = v.getOrElse(Vector())
    result
  }
  val oobHeight = 10000 m
  /**
   * depth required for the tile to count as a moisture producing tile
   */
  val moistureSpawnDepth = 0 m
  val map = Lazy2d.tabulate(cols, rows) {case (i, j) =>
    if (height.get(i, j).exists(_ <= moistureSpawnDepth)) {
      0
    } else {
      val path: Vector[Meters] = traceWind(i, j, steps, Vector())
      //path foreach {println(_)}
      val index = path.indexWhere( _ <= moistureSpawnDepth)
      if (index < 0) {
        0
      } else {
        0.5//(1 - index/steps.toDouble)/2

      }
    }
  }
}
