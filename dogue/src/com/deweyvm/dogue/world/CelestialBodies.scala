package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Angles, Circle}
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.badlogic.gdx.math.MathUtils
import com.deweyvm.dogue.Game

trait Season {
  def name:String
}
case object Summer extends Season {
  override def name = "Umr"
}
case object Winter extends Season {
  override def name = "Int"
}
case object Fall extends Season {
  override def name = "Fel"
}
case object Spring extends Season {
  override def name = "Sur"
}

case class DateConstants(framesPerDay:Int = 60*24,
                         daysPerYear:Int = 400,
                         monthsPerYear:Int = 10) {

}

case class Date(bodies:CelestialBodies, c:DateConstants) {
  private val t = bodies.t
  private val framesPerDay = 60*24
  private val daysPerYear = 400
  private val monthsPerYear = 10
  private val framesPerMonth = framesPerDay * (daysPerYear / monthsPerYear)
  private val framesPerYear = framesPerDay * daysPerYear

  def getTimeZone(i:Int, j:Int):Int = {
    0
  }

  def getString:String =  {
    val fps = Game.fps
    val year = t / framesPerYear
    val month = (t % framesPerYear)/framesPerMonth
    val day = t / framesPerDay
    val dayFrames = t % framesPerDay
    val framesPerHour = framesPerDay / 100.0
    val framesPerMinute = framesPerDay / (100.0 * 100.0)
    val framesPerSecond = framesPerDay / (100.0 * 100.0 * 100.0)

    val hours = ((dayFrames / framesPerHour) % 100).toInt
    val minutes = ((dayFrames / framesPerMinute) % 100).toInt
    val seconds = ((dayFrames / framesPerSecond) % 100).toInt
    "%dY %s %d %02d:%02d:%02d %s" format (year, months(month.toInt), day, seconds, minutes, hours, bodies.getSeason.name)

  }

  val months = List("Jar", "Bru", "Arc", "Pil", "Mie", "Nud", "Juy", "Gus", "Bep", "Bot")


}

case class CelestialBodies(t:Long, worldRadius:Double, c:DateConstants) {
  private val k = 9.0
  private val r = 1.0
  private val rotationRadius = worldRadius * 8 / 9
  private val precessionRadius = worldRadius / 10
  private val shadowRadius = worldRadius*(k+r)/k
  private def outer = Circle(worldRadius, worldRadius.dup + precessionPos)
  private def shadow = cover.copy(r = shadowRadius)
  private def cover = Circle(worldRadius, coverPos  + outer.center)

  private def precessionPos:Point2d = {
    val period = c.daysPerYear * c.framesPerDay
    Circle.cwPath(precessionRadius, period, t)
  }

  private def coverPos:Point2d = {
    val period = c.framesPerDay
    Circle.ccwPath(rotationRadius, period, t)
  }

  def date = Date(this, c)

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

  /**
   * how much heat is gained from the sun as you move away from the sun's closest point
   * @param i
   * @param j
   * @return
   */
  def getSunHeat(i:Int, j:Int):Double = {
    val light = getSunlight(i, j)
    val d = 1 - (Point2d(i, j) - outer.center).magnitude/worldRadius
    light + d
  }



  def update = this.copy(t = t + 1)
}
