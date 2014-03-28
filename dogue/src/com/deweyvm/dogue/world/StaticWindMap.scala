package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen.{VectorField, Arrow}
import com.deweyvm.dogue.common.data.{Angles, Array2d}
import scala.util.Random
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.DogueImplicits._

class StaticWindMap(noise:Array2d[Meters], heightMax:Double, windMax:Double, seed:Long) {
  private val dNoise = noise.map{case (i, j, m) => m.d }
  private val cols = noise.cols
  private val rows = noise.rows
  private val random = new Random(seed)
  private val xCenter = (random.nextDouble - 0.5)/10
  private val yCenter = (random.nextDouble - 0.5)/10
  private def pow(k:Double) = math.pow(k, 1.25)
  val arrows:Array2d[Arrow] = Array2d.tabulate(cols, rows) { case (i, j) =>
    val x = 0.5 - i/cols.toDouble + xCenter
    val y = 0.5 - j/rows.toDouble + yCenter
    val p = Point2d(x, y)
    val mag = pow(0.3) - pow(p.magnitude)
    val randRot = (random.nextDouble - 0.5)/2
    val norm = p.normalize.rotate(randRot)
    val grad = VectorField.gradient(dNoise, i.toInt, j.toInt).rotate(Angles.Tau/4 - 0.1)/heightMax
    val h = noise.get(i.toInt, j.toInt).d
    val heightMag:Double = (h > 0).select(h*windMax, 0.0)
    val result = (mag + heightMag) *: (grad + Point2d(norm.y, -norm.x))
    Arrow(result.normalize, result.magnitude)
  }
}
