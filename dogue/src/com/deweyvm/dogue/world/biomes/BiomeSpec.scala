package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.world.{AltitudinalRegion, LatitudinalRegion, SurfaceType}
import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.CommonImplicits.Rainfall

case class BiomeSpec(surface:SurfaceType,
                     `type`:BiomeType,
                     region:DogueRange[LatitudinalRegion],
                     moisture:DogueRange[Rainfall],
                     altitude:DogueRange[AltitudinalRegion])