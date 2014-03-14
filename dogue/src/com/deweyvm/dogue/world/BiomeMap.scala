package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.world.biomes.{Biome, Biomes}
import com.deweyvm.dogue.loading.{BiomeTypeManifest, Loader}
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
class BiomeMap(moisture:MoistureMap, surface:SurfaceMap, latitude:LatitudeMap) {
  val cols = moisture.cols
  val rows = moisture.rows

  Loader.fromFile[BiomeTypeManifest]("data/biomes/biometypes.manifest").verify

  val biomes:Array2d[Biome] = Array2d.tabulate(cols, rows) { case (i, j) =>
    val rain = moisture.get(i, j)
    val t = surface.landMap.get(i, j)
    val alt = Altitude.fromHeight(surface.heightMap.get(i, j))
    val lat = latitude.regions.get(i, j)
    Biomes.getBiome(rain, lat, alt, t)
  }
}
