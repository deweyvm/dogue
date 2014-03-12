package com.deweyvm.dogue.world.biomes

case class BiomeSpec(surface:SurfaceType,
                      region:DogueRange[LatitudinalRegion],
                      moisture:DogueRange[Rainfall],
                      altitude:DogueRange[AltitudinalRegion])
