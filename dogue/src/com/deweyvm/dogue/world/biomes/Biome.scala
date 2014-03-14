package com.deweyvm.dogue.world.biomes

import com.deweyvm.dogue.loading.{BiomeTypeManifest, BiomeLoader}
import com.deweyvm.dogue.DogueImplicits._

trait TerrestrialBiome
trait AquaticBiome
case class Biome(name:String,
                 spec:BiomeSpec) {
  val code = spec.`type`.code
  override def toString = name
  def fromLoader(manifest:BiomeTypeManifest, b:BiomeLoader) = {
    //val map = manifest.biomeTypes.map{ t => t.name -> t }
    /*val name = b.name.verify
    val `type` = b.`type`.read
    val minAlt = b.
    val maxAlt = b.
    val minLat = b.
    val maxLat = b.
    loader.*/
    1
  }
}
