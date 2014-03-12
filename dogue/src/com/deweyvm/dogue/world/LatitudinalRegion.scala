package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

object Latitude {

  val Polar         = LatitudinalRegion("Polar",          Color.Blue,      0.85 <=> 2.00)
  val Subpolar      = LatitudinalRegion("Subpolar",       Color.Cyan,      0.75 <=> 0.85)
  val Boreal        = LatitudinalRegion("Boreal",         Color.Teal,      0.60 <=> 0.75)
  val CoolTemperate = LatitudinalRegion("Cool Temperate", Color.DarkGreen, 0.35 <=> 0.60)
  val WarmTemperate = LatitudinalRegion("Warm Temperate", Color.Green,     0.15 <=> 0.35)
  val Subtropical   = LatitudinalRegion("Subtropical",    Color.Yellow,    0.05 <=> 0.15)
  val Tropical      = LatitudinalRegion("Tropical",       Color.Orange,    0.01 <=> 0.05)
  val SuperTropical = LatitudinalRegion("Super Tropical", Color.Red,       0.00 <=> 0.01)
  val All = Vector(Polar, Subpolar, Boreal, CoolTemperate, WarmTemperate, Subtropical, Tropical, SuperTropical)
  def getLatitude(r:Double) = All.find(_.range.contains(r)).getOrElse(Polar)
}

//http://en.wikipedia.org/wiki/File:Lifezones_Pengo.svg
case class LatitudinalRegion(name:String, color:Color, range:DogueRange[Double]) {
  override def toString = name
}

