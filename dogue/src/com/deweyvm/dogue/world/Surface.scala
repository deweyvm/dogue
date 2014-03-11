package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.Implicits.Meters
import com.deweyvm.dogue.common.data.{Array2d, Array2dView}
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.dogue.common.Implicits
import Implicits._

trait SurfaceType {
  val isWater:Boolean
}

object Surface {
  case object Land extends SurfaceType {
    val isWater = false
  }
  case object Water extends SurfaceType {
    val isWater = true
  }
}

class Surface(noise:Array2d[Double], params:WorldParams) {
  private val cols = noise.cols
  private val rows = noise.rows
  private def perlinToHeight(t:Double) = {
    if (t > 0) {
      val tm = 1 - t
      val sign = math.signum(t)
      1 - math.pow(tm, 0.1)
    } else {
      math.pow(t, 3)
    }
  }

  private val mountainSet = new TopoFeature(TopoFeature.mountain3, 50, params.size, params.period, params.octaves, params.seed)

  val numDepressions = 10
  private val lakeSet = new TopoFeature(TopoFeature.lake, numDepressions, params.size, 64, params.octaves, params.seed)


  private val (lakes, basins) = lakeSet.extracted.splitAt(numDepressions/2)

  private val lakeHeight:Array2d[Double] = noise.map {case (i, j, d) =>
    if (lakes.exists(_.contains((i, j)))) {
      lakeSet.get(i, j)
    } else {
      0
    }
  }

  private val basinHeight:Array2d[Double] = noise.map {case (i, j, d) =>
    if (basins.exists(_.contains((i, j)))) {
      lakeSet.get(i, j)
    } else {
      0
    }
  }

  private def applyMountain(land:Double, mountain:Double) = {
    (mountain > 0) select ((land + mountain)/2, land)
  }

  private val height = noise.map({ case (i, j, p) =>
    val base = perlinToHeight(p)
    val m = applyMountain(base, mountainSet.get(i, j))
    val h = m - lakeHeight.view.get(i, j) - basinHeight.view.get(i, j)
    h * 10000 m
  })



  private def isOcean(m:Meters):Boolean = m <= 0.0.m
  private def isLake(i:Int, j:Int):Boolean = lakes.exists {_.contains((i, j))}
  private val oceanPoints = {
    val buff = ArrayBuffer[(Int,Int)]()
    height foreach { case (i, j, h) =>
      if (isOcean(h) || isLake(i, j)) {
        (buff += ((i, j))).ignore()
      }
    }
    buff.toSet
  }

  def get(i:Int, j:Int):(SurfaceType, Meters) = landMap.view.get(i, j)

  private val flooded = FloodFill.extract(None, height.view, oceanPoints, isOcean)

  val landMap:Array2d[(SurfaceType, Meters)] = Array2d.tabulate(height.rows, height.cols) { case (i, j) =>
    if (flooded.exists{_.contains((i, j))}) {
      (Surface.Water, height.view.get(i, j))
    } else {
      (Surface.Land, height.view.get(i, j))
    }
  }
}
