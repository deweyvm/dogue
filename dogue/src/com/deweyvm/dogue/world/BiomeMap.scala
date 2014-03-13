package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.world.biomes.{Biome, Biomes}

class BiomeMap(cols:Int, rows:Int, moisture:MoistureMap, latitude:LatitudeMap) {
  val biomes:Array2d[Biome] = Array2d.tabulate(cols, rows) { case (i, j) =>
    val rain = moistureMap.get(i, j)
    val (t, _, alt) = getElevationParts(i, j)
    val lat = latitude.regions.get(i, j)
    Biomes.getBiome(rain, lat, alt, t)
  }
}
