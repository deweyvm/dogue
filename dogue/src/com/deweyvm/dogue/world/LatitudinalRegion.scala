package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.loading.Loader

object Latitude {
  val Polar         = LatitudinalRegion("Polar",          0.92 <=> 2.00)
  val Subpolar      = LatitudinalRegion("Subpolar",       0.85 <=> 0.92)
  val Boreal        = LatitudinalRegion("Boreal",         0.70 <=> 0.85)
  val CoolTemperate = LatitudinalRegion("Cool Temperate", 0.40 <=> 0.70)
  val WarmTemperate = LatitudinalRegion("Warm Temperate", 0.20 <=> 0.40)
  val Subtropical   = LatitudinalRegion("Subtropical",    0.10 <=> 0.20)
  val Tropical      = LatitudinalRegion("Tropical",       0.03 <=> 0.10)
  val SuperTropical = LatitudinalRegion("Super Tropical", 0.00 <=> 0.03)
  val All = Vector(Polar, Subpolar, Boreal, CoolTemperate, WarmTemperate, Subtropical, Tropical, SuperTropical)
  val Map = All.map(l => l.name -> l).toMap
  def getLatitude(r:Double) = All.find(_.range.contains(r)).getOrElse(Polar)
}

case class LatitudinalRegion(name:String, range:DogueRange[Double]) {
  override def toString = name
  val color = Color.fromHsb(range.min.toFloat/2)
}

