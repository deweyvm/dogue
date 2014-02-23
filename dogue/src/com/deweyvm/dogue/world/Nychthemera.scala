package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Angles, Circle}
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.badlogic.gdx.math.MathUtils

trait Season
case object Summer extends Season
case object Winter extends Season
case object Fall extends Season
case object Spring extends Season


case class Nychthemera(t:Long, radius:Double) {
  private val k = 9.0
  private val r = 1.0
  private val rotationRadius = radius * 8 / 9
  private val precessionRadius = radius / 10
  private val shadowRadius = radius*(k+r)/k
  private def outer = Circle(radius, radius.dup + precessionPos)
  private def shadow = cover.copy(r = shadowRadius)
  private def cover = Circle(radius, coverPos  + outer.center)
  private def precessionPos:Point2d = {
    val period = 50000
    Circle.cwPath(precessionRadius, period, t)
  }

  private def coverPos:Point2d = {
    val period = 600
    Circle.ccwPath(rotationRadius, period, t)
  }

  def getSunlight(i:Int, j:Int):Double = {
    val p = Point2d(i, j)
    if ((p - outer.center).magnitude < 4096/8) {
      0.25
    } else {
      if (!cover.contains(p) && shadow.contains(p)) {
        val d = (p - cover.center).magnitude/(shadow.r - cover.r)
        1 - d + k/r
      } else if (!cover.contains(p)){
        0
      } else {
        1
      }
    }

  }

  /**
   * how much heat is gained from the sun as you move away from the sun's closest point
   * @param i
   * @param j
   * @return
   */
  def getSunHeat(i:Int, j:Int):Double = {
    val light = getSunlight(i, j)
    val d = 1 - (Point2d(i, j) - outer.center).magnitude/radius
    light + d
  }

  def getSeason:Season = {
    val angle = (precessionPos.angle + Angles.Tau) % Angles.Tau
    if (angle < Angles.Tau/4) {
      Fall
    } else if (angle < Angles.Tau/2) {
      Winter
    } else if (angle < 3*Angles.Tau/4) {
      Spring
    } else {
      Summer
    }

  }

  def update = Nychthemera(t + 1, radius)
}
