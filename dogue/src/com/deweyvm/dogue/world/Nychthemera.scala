package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Angles, Circle}
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._

case class Nychthemera(t:Int, radius:Double) {
  private val outer = Circle(radius, Point2d(radius, radius))
  private def shadow = Circle(radius, shadowPos + precessionPos + center)
  private def precessionPos:Point2d = {
    val period = 500
    val a = t*Angles.Tau/period
    val x = precessionRadius * math.sin(a)
    val y = precessionRadius * math.cos(a)
    Point2d(x, y)
  }
  private val rotationRadius = radius * 8 / 9
  private val precessionRadius = radius / 10
  private val center = Point2d(radius, radius)
  private def shadowPos:Point2d = {
    val period = 60
    val a = t*Angles.Tau/period
    val x = rotationRadius * math.sin(a)
    val y = rotationRadius * math.cos(a)
    Point2d(x, y)
  }

  def getLight(i:Int, j:Int):Double = {
    val p = Point2d(i, j)
    if ((p - (precessionPos + center)).magnitude < 4098/8) {
      0.25
    } else {
      (shadow.contains(Point2d(i, j)) && outer.contains(Point2d(i, j))).select(1,0)
    }

  }

  def update = Nychthemera(t + 1, radius)
}
