package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.loading.Loader

case class LatitudinalRegion(name:String, range:DogueRange[Double]) {
  override def toString = name
  val color = Color.fromHsb(range.min.toFloat/2)
}

object Latitude {
  val Void = LatitudinalRegion("void", -100.0 <=> 100.0)
}

