package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.DogueRange
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.gleany.graphics.Color

trait TerrestrialBiome
trait AquaticBiome
case class Biome(name:String,
                 mapColor:Color,
                 region:DogueRange[LatitudinalRegion],
                 moisture:DogueRange[Double],
                 temperature:DogueRange[Double],
                 height:DogueRange[AltitudinalRegion]) {
  override def toString = name
}

object Biome {
  val Void = Biome("Void", Color.Black, Latitude.Polar <=> Latitude.SuperTropical, 0.0 <=> 1.0, -273.0 <=> 1000.0, Altitude.Abyss <=> Altitude.SuperAlpine)
  /**
   * T: [-5, 5]
   * M: [200,750]
   */
  //val Taiga = Biome("Taiga", Color.DarkGreen, Boreal, 200.0 <=> 750.0, -5.0 <=> 5.0, 0.m <=> 10.m)
  /**
   * T:27
   * M:[500-1300]
   */
  //val TropicalSavanna = Biome("Tropical Savanna", Boreal, 500.0 <=> 1300.0, 17.0 <=> 37.0, 0.m <=> 10.m)
  /**
   * arctic, alpine, anarctic
   * T:[-50,12]
   * M:[15,25]
   */

  //trait Tundra extends TerrestrialBiome


  /**
   *
   */
  /*trait TemperateSavanna extends TerrestrialBiome
  trait Steppe extends TerrestrialBiome
  trait Badlands extends TerrestrialBiome
  trait XericScrubland extends TerrestrialBiome
  trait DryDesert extends TerrestrialBiome
  trait PolarDesert extends TerrestrialBiome
  trait Alpine extends TerrestrialBiome
  trait TemperateDeciduousForest extends TerrestrialBiome
  trait TropicalDeciduousForest extends TerrestrialBiome
  trait TemperateRainforest extends TerrestrialBiome
  trait TropicalRainforest extends TerrestrialBiome
  trait Mangrove extends TerrestrialBiome
  trait TemperateWetlands extends AquaticBiome
  trait TropicalWetlands extends AquaticBiome*/

}

/*class BiomeMap(moisture:Array2dView[Double], ) {
  def get(i:Int, j:Int)
}*/
