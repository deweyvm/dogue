package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.common.procgen.PerlinNoise
import com.deweyvm.gleany.graphics.ImageUtils
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.graphics.OglRenderer
import com.badlogic.gdx.graphics.g2d.Sprite
import com.deweyvm.dogue.common.Implicits
import Implicits._
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.dogue.Game

class PerlinVisualizer extends Visualizer {
  override def zoom = 0.5
  override def translation = (-Game.RenderWidth/4, -Game.RenderHeight/4)
  val size = 256
  val noise = new PerlinNoise(1/32f, 5, size, 0).render
  val start = noise.find{_ > 0}.map {case (i, j, t) => (i, j)}.getOrElse(throw new RuntimeException)
  val flood = new FloodFiller[Double](noise, _ > 0, start._1, start._2)
  val floodPoints = flood.fill(15,15,5)
  val newNoise = noise.view.map{ case (i, j, t) =>
    if (floodPoints.contains((i, j))) {
      10
    } else {
      t
    }
  }
  val mapped = newNoise map { case (i, j, d) =>
    math.abs(d) - 1.0
  }//fixme issue #254
  //val texture = ImageUtils.makeGreyscaleTexture(raw.toVector, size, size)
  override def drawBatch(ogl:OglRenderer) = {
    //ogl.drawSprite(new Sprite(texture), 10, 10)
  }
}

/**
 *
 * @param a the array to be filled
 * @param f function returning whether an element is "solid" or not
 * @param sx start x index
 * @param sy start y index
 */
class FloodFiller[T](a:Array2d[T], f:T => Boolean, sx:Int, sy:Int) {
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
    def get(x:Int, y:Int):Option[T] = {
      if (x < i || x > sx + width - 1 || y < j || y > sy + height - 1 || points.contains((x, y))) {
        None
      } else {
        a.get(x, y)
      }
    }
    Vector(
      (i - step, j),
      (i + step, j),
      (i, j + step),
      (i, j - step)
    ).flatMap { case (ii, jj) =>
      get(ii,jj) map {(_, (ii, jj))}
    }.filter { case (t, (ii, jj)) =>
      f(t)
    }.map {_._2}
  }
}
