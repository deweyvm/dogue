package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Angles, Circle}
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._

case class Nychthemera(t:Int, radius:Double) {
  private def outer = Circle(radius, radius.dup + precessionPos)
  private def shadow = Circle(radius, shadowPos  + outer.center)
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

  def getSunlight(i:Int, j:Int):Double = {
    val p = Point2d(i, j)
    if ((p - (precessionPos + center)).magnitude < 4098/8) {
      0.25
    } else {
      (shadow.contains(p) && outer.contains(p)).select(1,0)
    }

  }

  def getSunHeat(i:Int, j:Int):Double = {
    0
  }

  def update = Nychthemera(t + 1, radius)
}
