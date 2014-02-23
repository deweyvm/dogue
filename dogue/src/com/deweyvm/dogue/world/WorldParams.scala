package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen.MapName

case class WorldParams(minimapSize:Int, period:Int, octaves:Int, size:Int, seed:Long) {
  val name = new MapName(seed).makeName
}