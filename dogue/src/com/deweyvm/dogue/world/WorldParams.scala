package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen.{PerlinParams, MapName}



case class WorldParams(minimapSize:Int,
                       perlin:PerlinParams,
                       date:DateConstants) {
  val size = perlin.size
  val seed = perlin.seed
  val name = new MapName(seed).makeName
}
