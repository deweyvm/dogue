package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color


object LatitudinalRegion {
  val All = Vector(Polar, Subpolar, Boreal, CoolTemperate, WarmTemperate, Subtropical, Tropical)

  /**
   * radius on 0..1
   */
  def getRegion(r:Double):LatitudinalRegion = {
    val k = (r * 100).toInt
    if (k < 5) {
      Tropical
    } else if (k < 15) {
      Subtropical
    } else if (k < 35) {
      WarmTemperate
    } else if (k < 60) {
      CoolTemperate
    } else if (k < 75) {
      Boreal
    } else if (k < 85) {
      Subpolar
    } else {
      Polar
    }
  }
}

//http://en.wikipedia.org/wiki/File:Lifezones_Pengo.svg
trait LatitudinalRegion { def color:Color }

case object Polar extends LatitudinalRegion {
  def color = Color.Blue
}
case object Subpolar extends LatitudinalRegion  {
  def color = Color.Cyan
}
case object Boreal extends LatitudinalRegion {
  def color = Color.Teal
}
case object CoolTemperate extends LatitudinalRegion {
  def color = Color.DarkGreen
}
case object WarmTemperate extends LatitudinalRegion {
  def color = Color.Green
}
case object Subtropical extends LatitudinalRegion {
  def color = Color.Yellow
}
case object Tropical extends LatitudinalRegion {
  def color = Color.Orange
}
