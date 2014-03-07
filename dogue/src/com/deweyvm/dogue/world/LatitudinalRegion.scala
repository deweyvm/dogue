package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color


object Latitude {

  val Polar         = LatitudinalRegion("Polar", Color.Blue)
  val Subpolar      = LatitudinalRegion("Subpolar", Color.Cyan)
  val Boreal        = LatitudinalRegion("Boreal", Color.Teal)
  val CoolTemperate = LatitudinalRegion("Cool Temperate", Color.DarkGreen)
  val WarmTemperate = LatitudinalRegion("Warm Temperate", Color.Green)
  val Subtropical   = LatitudinalRegion("Subtropical", Color.Yellow)
  val Tropical      = LatitudinalRegion("Tropical", Color.Orange)
  val SuperTropical = LatitudinalRegion("Super Tropical", Color.Red)
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
case class LatitudinalRegion(name:String, color:Color) {
  override def toString = name
}

