package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen.{PerlinParams, PerlinNoise}
import com.deweyvm.dogue.common.data.Array2dView
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._

object TopoFeature {
  def river(d:Double) = math.abs(d) - 0.15//32, 6, 256
  def mountain(d:Double) = (1 - math.abs(d)) - 0.95//32, 6, 256
  def mountain2(d:Double) = math.pow(d, 0.1) - 0.85//16, 6, 256
  def mountain3(d:Double) = {
    val h = 1 - (d - 0.3).clamp(0, 1)
    1 - math.pow(h, 5)
  }

  def lake(d:Double) = {
    val h = 1 - (d - 0.5).clamp(0, 1)
    (1 - math.pow(h, 3))/2
  }
}

class TopoFeature(f:Double => Double, count:Int, perlin:PerlinParams) {
  val size = perlin.size
  val rows = size
  val cols = size

  val noise = new PerlinNoise(perlin).render.map { case (i, j, d) =>
    f(d)
  }.view

  val all:Set[(Int,Int)] = {
    val points = for (i <- 0 until cols; j <- 0 until rows) yield {
      (i, j)
    }
    val solid = points.filter{ case (i, j) => noise.get(i, j) > 0 }
    Set(solid:_*)
  }

  val extracted:Vector[Set[(Int,Int)]] = FloodFill.extract(count.some, noise, all, (x:Double) => x > 0.0)

  def get(i:Int, j:Int):Double = {
    view.get(i, j)
  }

  val view = noise.map {case (i, j, d) =>
    if (extracted.exists{_.contains((i, j))}) {
      d
    } else {
      0
    }
  }

  def getNoise(b:Boolean):Array2dView[Color] = noise.map {case (i, j, d) =>
    if (extracted.exists{_.contains((i, j))}) {
      val c = d.toFloat
      if (b) {

        Color(c, c, 0, 1)
      } else {

        Color(c, 0, c, 1)
      }
    } else {
      Color(0,0,0,0)
    }
  }
}

object FloodFill {
  type Points = Set[(Int,Int)]
  def extract[T](limit:Option[Int], v:Array2dView[T], emptyPoints:Points, f:T=>Boolean) = {
    val (regions, _) = extractHelper(limit.getOrElse(Int.MaxValue), v, f, emptyPoints, Vector())
    regions
  }

  private def extractHelper[T](limit:Int, v:Array2dView[T], f:T=>Boolean, emptyPoints:Points, current:Vector[Points]):(Vector[Points], Points) = {
    emptyPoints.headOption match {
      case Some(root) if limit > 0 =>
        val filled = new FloodFill(v, f, root._1, root._2).fill(v.cols, v.rows, 1)
        extractHelper(limit - 1, v, f, emptyPoints -- filled, filled +: current)
      case _ =>
        (current, emptyPoints)
    }
  }
}
/**
 *
 * @param a the array to be filled
 * @param f function returning whether an element is "empty" or not
 * @param sx start x index
 * @param sy start y index
 */
class FloodFill[T](a:Array2dView[T], f:T => Boolean, sx:Int, sy:Int) {
  private var points = Set[(Int,Int)]((sx, sy))
  private val workQueue = collection.mutable.Queue[(Int,Int)](points.toVector:_*)

  def fill(width:Int, height:Int, step:Int):Set[(Int,Int)] = {
    while (!workQueue.isEmpty) {
      val next = workQueue.dequeue()
      val neighbors = getNeighbors(next._1, next._2, width, height, step)
      points = points ++ neighbors
      workQueue ++= neighbors
    }
    points
  }

  private def getNeighbors(i:Int, j:Int, width:Int, height:Int, step:Int):Seq[(Int,Int)] = {
    def get(x:Int, y:Int):Option[(T,(Int,Int))] = {
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1 || points.contains((x, y))) {
        None
      } else {
        (a.get(x, y), (x, y)).some
      }
    }
    Vector(
      (i - step, j),
      (i + step, j),
      (i, j + step),
      (i, j - step)
    ).map { case (ii, jj) =>
      get(ii,jj)
    }.flatten.filter { case (t, (ii, jj)) =>
      f(t)
    }.map {_._2}
  }
}
