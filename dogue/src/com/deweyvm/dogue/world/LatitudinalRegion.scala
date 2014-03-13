package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

object Latitude {

  val Polar         = LatitudinalRegion("Polar",          Color.Blue,      0.92 <=> 2.00)
  val Subpolar      = LatitudinalRegion("Subpolar",       Color.Cyan,      0.85 <=> 0.92)
  val Boreal        = LatitudinalRegion("Boreal",         Color.Teal,      0.70 <=> 0.85)
  val CoolTemperate = LatitudinalRegion("Cool Temperate", Color.DarkGreen, 0.40 <=> 0.70)
  val WarmTemperate = LatitudinalRegion("Warm Temperate", Color.Green,     0.20 <=> 0.40)
  val Subtropical   = LatitudinalRegion("Subtropical",    Color.Yellow,    0.10 <=> 0.20)
  val Tropical      = LatitudinalRegion("Tropical",       Color.Orange,    0.03 <=> 0.10)
  val SuperTropical = LatitudinalRegion("Super Tropical", Color.Red,       0.00 <=> 0.03)
  val All = Vector(Polar, Subpolar, Boreal, CoolTemperate, WarmTemperate, Subtropical, Tropical, SuperTropical)
  def getLatitude(r:Double) = All.find(_.range.contains(r)).getOrElse(Polar)
}

//http://en.wikipedia.org/wiki/File:Lifezones_Pengo.svg
case class LatitudinalRegion(name:String, color:Color, range:DogueRange[Double]) {
  override def toString = name
}

