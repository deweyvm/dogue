package com.deweyvm.dogue.world.biomes

trait BiomeResolver {
   val params:Seq[Biome]
   def resolve:Biome
 }
