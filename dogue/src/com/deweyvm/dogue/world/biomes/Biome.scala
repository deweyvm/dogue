package com.deweyvm.dogue.world.biomes

case class Biome(name:String,
                 spec:BiomeSpec) {
  val code = spec.`type`.code
  override def toString = name
}
