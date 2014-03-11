package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, DogueRange}
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.gleany.graphics.Color

trait TerrestrialBiome
trait AquaticBiome
case class Biome(name:String,
                 mapColor:Color,
                 code:Code,
                 region:DogueRange[LatitudinalRegion],
                 moisture:DogueRange[Rainfall],
                 temperature:DogueRange[Celcius],
                 altitude:DogueRange[AltitudinalRegion]) {
  override def toString = name
}
//moisture 0 - 10000mm/year
object Biome {
  val Void = Biome("Void", Color.Black, Code.` `,
    Latitude.Polar <=> Latitude.SuperTropical,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Abyss <=> Altitude.SuperAlpine)

  def getBiome(wet:Rainfall, temp:Double, lat:LatitudinalRegion, alt:AltitudinalRegion):Biome = {
    All.find {b =>
      b.region.contains(lat) &&
      b.moisture.contains(wet) &&
      b.temperature.contains(temp) &&
      b.altitude.contains(alt)
    }.getOrElse(Void)
  }

  val AlpineTundra = Biome("Alpine Tundra", Color.White.dim(1.1f), Code.▲,
    Latitude.Tropical <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Alpine <=> Altitude.SuperAlpine)

  val ArcticTundra = Biome("Arctic Tundra", Color.DarkPurple, Code.☼,
    Latitude.Subpolar <=> Latitude.Subpolar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.SuperAlpine
  )

  val AntarcticTundra = Biome("Antarctic Tundra", Color.DarkPurple.dim(1.2f), Code.☼,
    Latitude.Polar <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.SuperAlpine
  )

  val Taiga = Biome("Taiga", Color.DarkGreen.dim(1.2f), Code.*,
    Latitude.Boreal <=> Latitude.Boreal,
    0.`mm/yr` <=> 10000.`mm/yr`,//M: [200,750]
    -273.C <=> 1000.C,//T: [-5, 5]
    Altitude.Midlands <=> Altitude.Subalpine)

  val TropicalSavanna = Biome("Tropical Savanna", Color.Orange, Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    500.`mm/yr` <=> 1300.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Lowlands <=> Altitude.Highlands)


  //http://answers.yahoo.com/question/index?qid=20120201161323AAOwMtY
  val TemperateDeciduousForest = Biome("Temperate Deciduous Forest", Color.DarkGreen, Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    750.`mm/yr` <=> 1500.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Hellscape = Biome("Hellscape", Color.Red, Code.x,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    0.`mm/yr` <=> 0.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Infestation = Biome("Infestation", Color.Green, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    0.01.`mm/yr` <=> 2000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Maelstrom = Biome("Infestation", Color.Green, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    2000.`mm/yr` <=> 5000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Roil = Biome("Infestation", Color.Green, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    5000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val All = Vector[Biome](Taiga, AlpineTundra, ArcticTundra, AntarcticTundra, TropicalSavanna, TemperateDeciduousForest, Hellscape, Infestation, Maelstrom, Roil)

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
