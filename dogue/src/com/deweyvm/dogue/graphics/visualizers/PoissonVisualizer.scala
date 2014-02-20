package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.common.procgen.{PerlinNoise, PoissonRng}
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.graphics.OglRenderer
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
class PoissonVisualizer {
  val size = 500
  val space = 5
  val maxSpace = space
  var seed = 0L
  def makePerlin = new PerlinNoise(1/128.0, 5, size, seed).lazyRender
  def makePoisson = {
    val pos = new PoissonRng(size, size, {case(i, j) => space}, maxSpace, seed).getPoints
    val points = pos.filter { pt =>
      noise.get(pt.x.toInt, pt.y.toInt) match {
        case Some(d) => d > 0.2
        case None => true
      }
    }
    println(points.length)
    points

  }
  var noise = makePerlin
  var poisson = makePoisson
  def render(r:OglRenderer) {
    if (Controls.Space.justPressed) {
      seed += 1
      noise = makePerlin
      poisson = makePoisson
    }
    val x = 50
    val y = 50
    r.translateShape(x, y){() =>
      poisson foreach { pt =>
        r.drawPoint(pt, Color.White)
      }
    }
  }
}
