package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.DogueImplicits._
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
case class AltitudinalRegion(name:String, range:DogueRange[Meters]) {
  override def toString = name
}


object Altitude {
  val Void = AltitudinalRegion("void", -10000.0.m <=> 10000.0.m)
}

