package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, DogueRange}
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.gleany.graphics.Color
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.dogue.common.reflect.Reflection

trait TerrestrialBiome
trait AquaticBiome
case class Biome(name:String,
                 mapColor:Color,
                 code:Code,
                 surface:SurfaceType,
                 region:DogueRange[LatitudinalRegion],
                 moisture:DogueRange[Rainfall],
                 temperature:DogueRange[Celcius],
                 altitude:DogueRange[AltitudinalRegion]) {
  override def toString = name
}
//moisture 0 - 10000mm/year
object Biomes {

  def LandBiome(name:String,
                mapColor:Color,
                code:Code,
                region:DogueRange[LatitudinalRegion],
                moisture:DogueRange[Rainfall],
                temperature:DogueRange[Celcius],
                altitude:DogueRange[AltitudinalRegion]) = {
    Biome(name, mapColor, code, Surface.Land, region, moisture, temperature, altitude)
  }

  def AquaticBiome(name:String,
                   mapColor:Color,
                   code:Code,
                   region:DogueRange[LatitudinalRegion],
                   moisture:DogueRange[Rainfall],
                   temperature:DogueRange[Celcius],
                   altitude:DogueRange[AltitudinalRegion]) = {
    Biome(name, mapColor, code, Surface.Water, region, moisture, temperature, altitude)
  }

  val Void = LandBiome("Void", Color.Black, Code.` `,
    Latitude.SuperTropical <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Abyss <=> Altitude.SuperAlpine)

  def getBiome(wet:Rainfall, temp:Double, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType):Biome = {
    All.find {b =>
      b.surface == surf &&
      b.region.contains(lat) &&
      b.moisture.contains(wet) &&
      b.temperature.contains(temp) &&
      b.altitude.contains(alt)
    }.getOrElse {
      /*println("Void region:")
      println("    Moisture : %.2f" format wet.d)
      println("    Latitude : %s" format lat)
      println("    Altitude : %s" format alt)
      println("    Surfacet : %s" format surf)*/

      Void
    }
  }

  val Lake = AquaticBiome("Lake", Color.Blue.brighten(0.1f), Code.≈,
    Latitude.SuperTropical <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.SuperAlpine)

  val Ocean = AquaticBiome("Ocean", Color.Blue, Code.≈,
    Latitude.SuperTropical <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Abyss <=> Altitude.Oceanic)

  val AlpineTundra = LandBiome("Alpine Tundra", Color.White.dim(1.1f), Code.▲,
    Latitude.Tropical <=> Latitude.Boreal,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Alpine <=> Altitude.SuperAlpine)

  val ArcticTundra = LandBiome("Arctic Tundra", Color.White.dim(1.5f), Code.☼,
    Latitude.Subpolar <=> Latitude.Subpolar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Subalpine
  )

  val AntarcticTundra = LandBiome("Antarctic Tundra", Color.White.dim(1.3f), Code.☼,
    Latitude.Polar <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Subalpine
  )

  val Glacier = LandBiome("Glacier", Color.Pink, Code.☼,
    Latitude.Tropical <=> Latitude.Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Subalpine <=> Altitude.SuperAlpine
  )

  val BarrenCliffs = LandBiome("Barren Cliffs", Color.Purple.dim(1.6f), Code.^,
    Latitude.Tropical <=> Latitude.Boreal,
    0.`mm/yr` <=> 25.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Montane <=> Altitude.SuperAlpine
  )

  val Taiga = LandBiome("Taiga", Color.DarkGreen.dim(1.2f), Code.*,
    Latitude.Boreal <=> Latitude.Boreal,
    250.`mm/yr` <=> 10000.`mm/yr`,//M: [200,750]
    -273.C <=> 1000.C,//T: [-5, 5]
    Altitude.Midlands <=> Altitude.Subalpine)

  val BorealWetlands = LandBiome("Boreal Wetlands", Color.DarkGreen.dim(1.5f), Code.*,
    Latitude.Boreal <=> Latitude.Boreal,
    250.`mm/yr` <=> 10000.`mm/yr`,//M: [200,750]
    -273.C <=> 1000.C,//T: [-5, 5]
    Altitude.Lowlands <=> Altitude.Lowlands)

  val BorealGrassland = LandBiome("Boreal Grassland", Color.DarkGreen.dim(1.8f), Code.*,
    Latitude.Boreal <=> Latitude.Boreal,
    25.`mm/yr` <=> 250.`mm/yr`,//M: [200,750]
    -273.C <=> 1000.C,//T: [-5, 5]
    Altitude.Lowlands <=> Altitude.Montane)

  val TropicalSavanna = LandBiome("Tropical Savanna", Color.Orange, Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    500.`mm/yr` <=> 1300.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Lowlands <=> Altitude.Highlands)

  val PeatSwampForest = LandBiome("Peat Swamp Forest", Color.Orange.dim(1.5f), Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    1300.`mm/yr` <=> 2000.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Lowlands <=> Altitude.Lowlands)

  val SubtropicalDeciduousForest = LandBiome("Subtropical Deciduous Forest", Color.Green.dim(1.5f), Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    1300.`mm/yr` <=> 2000.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Midlands <=> Altitude.Highlands)

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TropicalRainforest = LandBiome("Tropical Rainforest", Color.Green.brighten(0.1f), Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Midlands <=> Altitude.Highlands)

  val Mangroves = LandBiome("Mangroves", Color.Brown.brighten(0.1f), Code.*,
    Latitude.Tropical <=> Latitude.Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Lowlands)

  val MontaneRainforest = LandBiome("Montane Rainforest", Color.Green.dim(1.7f), Code.*,
    Latitude.Subtropical <=> Latitude.CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Montane <=> Altitude.Subalpine)

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TemperateRainforest = LandBiome("Temperate Rainforest", Color.Green.dim(1.4f), Code.*,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,//17.0 <=> 37.0, 0.m <=> 10.m)
    Altitude.Midlands <=> Altitude.Highlands)

  val TemperateBroadleafForest = LandBiome("Temperate Broadleaf Forest", Color.DarkGreen.brighten(0.1f), Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Midlands <=> Altitude.Montane
  )

  //http://answers.yahoo.com/question/index?qid=20120201161323AAOwMtY
  val TemperateDeciduousForest = LandBiome("Temperate Deciduous Forest", Color.DarkGreen, Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    750.`mm/yr` <=> 1500.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Midlands <=> Altitude.Montane
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/bioconiferous.php
  val TemperateConiferousForest = LandBiome("Temperate Coniferous Forest", Color.DarkGreen.dim(2), Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    300.`mm/yr` <=> 750.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Midlands <=> Altitude.Subalpine
  )

  val TemperateWetlands = LandBiome("Temperate Wetlands", Color.Brown.dim(2), Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    180.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Lowlands
  )

  val TemperateScrubland = LandBiome("Temperate Scrubland", Color.Yellow.dim(2), Code.♠,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    25.`mm/yr` <=> 180.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Lowlands
  )

  //approximately 1 in. (.25 cm) of rain falls in dry deserts per year.
  //The latitude range is 15-28° north and south of the equator.
  //http://www.blueplanetbiomes.org/desert_climate_page.htm
  val AridDesert = LandBiome("Arid Desert", Color.Yellow, Code.`.`,
    Latitude.Tropical <=> Latitude.CoolTemperate,
    0.`mm/yr` <=> 25.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  //made up
  val Badlands = LandBiome("Badlands", Color.Brown, Code.`.`,
    Latitude.CoolTemperate <=> Latitude.Boreal,
    0.`mm/yr` <=> 250.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Steppe = LandBiome("Steppe", Color.Brown.brighten(0.3f), Code.`.`,
    Latitude.Subtropical <=> Latitude.CoolTemperate,
    250.`mm/yr` <=> 750.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val XericScrubland = LandBiome("Xeric Scrubland", Color.Yellow.dim(1.5f), Code.`,`,
    Latitude.Tropical <=> Latitude.Subtropical,
    25.`mm/yr` <=> 250.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val TallGrassland = LandBiome("Tall Temperate Grassland", Color.Green.dim(2f), Code.`,`,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    250.`mm/yr` <=> 300.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val MontaneGrassland = LandBiome("Montane Grassland", Color.Green.dim(4f), Code.`,`,
    Latitude.Subtropical <=> Latitude.Boreal,
    25.`mm/yr` <=> 300.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Montane <=> Altitude.Alpine
  )

  val ShortGrassland = LandBiome("Short Temperate Grassland", Color.Green.dim(3f), Code.`,`,
    Latitude.WarmTemperate <=> Latitude.CoolTemperate,
    25.`mm/yr` <=> 250.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Hellscape = LandBiome("Hellscape", Color.Red, Code.x,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    0.`mm/yr` <=> 0.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Infestation = LandBiome("Infestation", Color.Green, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    0.01.`mm/yr` <=> 2000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Maelstrom = LandBiome("Maelstrom", Color.Grey, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    2000.`mm/yr` <=> 7000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val Roil = LandBiome("Infestation", Color.Cyan, Code.♣,
    Latitude.SuperTropical <=> Latitude.SuperTropical,
    7000.`mm/yr` <=> 10000.`mm/yr`,
    -273.C <=> 1000.C,
    Altitude.Lowlands <=> Altitude.Highlands
  )

  val All:Vector[Biome] = Reflection.getEnum(Biomes, this.getClass, "Biome", _ == "Void")


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
