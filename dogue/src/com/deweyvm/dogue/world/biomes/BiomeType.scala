package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.common.reflect.Reflection


case class BiomeType(name:String, baseHue:Double, code:Code)

object BiomeType {
  val Void = BiomeType("void", 0, Code.`?`)
}

