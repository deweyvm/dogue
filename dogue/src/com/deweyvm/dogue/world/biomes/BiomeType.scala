package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.common.reflect.Reflection


case class BiomeType(name:String, baseHue:Double, code:Code)

object BiomeType {
  val Desert    = BiomeType("Desert", 0.1, Code.`.`)
  val Grassland = BiomeType("Grassland", 0.15, Code.`»`)
  val Forest    = BiomeType("Forest", 0.2, Code.♠)
  val Wetlands  = BiomeType("Wetlands", 0.45, Code.~)
  val Alpine    = BiomeType("Alpine", 0.55, Code.▲)
  val Aquatic   = BiomeType("Aquatic", 0.65, Code.≈)
  val Special   = BiomeType("Special", 0.75, Code.`¢`)

  val All = Reflection.getEnum(BiomeType, this.getClass, "BiomeType", x => true)
}

