package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.common.reflect.Reflection


case class BiomeType(baseHue:Double, code:Code)

object BiomeType {
  val Desert = BiomeType(0.1, Code.`.`)
  val Grassland = BiomeType(0.15, Code.`»`)
  val Forest = BiomeType(0.2, Code.♠)
  val Wetlands = BiomeType(0.45, Code.~)
  val Alpine = BiomeType(0.55, Code.▲)
  val Aquatic = BiomeType(0.65, Code.≈)
  val Special = BiomeType(0.75, Code.`¢`)

  val All = Reflection.getEnum(BiomeType, this.getClass, "BiomeType", x => true)
}

