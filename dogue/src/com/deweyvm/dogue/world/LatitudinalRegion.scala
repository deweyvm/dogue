package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color


object Latitude {

  val Polar = LatitudinalRegion(Color.Blue)
  val Subpolar = LatitudinalRegion(Color.Cyan)
  val Boreal = LatitudinalRegion(Color.Teal)
  val CoolTemperate = LatitudinalRegion(Color.DarkGreen)
  val WarmTemperate = LatitudinalRegion(Color.Green)
  val Subtropical = LatitudinalRegion(Color.Yellow)
  val Tropical = LatitudinalRegion(Color.Orange)
  val SuperTropical = LatitudinalRegion(Color.Red)
  val All = Vector(Polar, Subpolar, Boreal, CoolTemperate, WarmTemperate, Subtropical, Tropical)


  /**
   * radius on 0..1
   */
  def getRegion(r:Double):LatitudinalRegion = {
    val k = (r * 100).toInt
    if (k < 1) {
      SuperTropical
    } else if (k < 5) {
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
case class LatitudinalRegion(color:Color)

