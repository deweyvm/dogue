package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.common.data.Code
import com.deweyvm.gleany.graphics.Color

trait TerrestrialBiome
trait AquaticBiome
case class Biome(name:String,
                 mapColor:Color,
                 code:Code,
                 spec:BiomeSpec) {
  override def toString = name
}
