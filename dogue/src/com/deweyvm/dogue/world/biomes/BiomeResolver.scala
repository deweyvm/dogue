package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.world.{SurfaceType, AltitudinalRegion, LatitudinalRegion}
import com.deweyvm.dogue.DogueImplicits.Rainfall

trait BiomeResolver {
  def resolve(wet:Rainfall, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType, bs:Seq[Biome]):Biome
  def printConflicts():Unit
}
