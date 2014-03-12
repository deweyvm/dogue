package com.deweyvm.dogue.world.biomes

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{DogueRange, Code}
import com.deweyvm.dogue.world._
import com.deweyvm.dogue.common.reflect.Reflection
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.dogue.common.Implicits
import Implicits._

object Biomes {

  def LandBiome(name:String,
                mapColor:Color,
                code:Code,
                region:DogueRange[LatitudinalRegion],
                moisture:DogueRange[Rainfall],
                altitude:DogueRange[AltitudinalRegion]) = {
    val spec = BiomeSpec(Surface.Land, region, moisture, altitude)
    Biome(name, mapColor, code, spec)
  }

  def AquaticBiome(name:String,
                   mapColor:Color,
                   code:Code,
                   region:DogueRange[LatitudinalRegion],
                   moisture:DogueRange[Rainfall],
                   altitude:DogueRange[AltitudinalRegion]) = {
    val spec = BiomeSpec(Surface.Water, region, moisture, altitude)
    Biome(name, mapColor, code, spec)
  }

  def getBiome(wet:Rainfall, temp:Double, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType):Biome = {
    val found = All.filter { b =>
      b.spec.surface == surf &&
      b.spec.region.contains(lat) &&
      b.spec.moisture.contains(wet) &&
      b.spec.altitude.contains(alt)
    }
    if (found.length > 1) {

      println("(%s) found for :" format found.mkString(","))
      println("    Moisture : %.2f" format wet.d)
      println("    Latitude : %s" format lat)
      println("    Altitude : %s" format alt)
      println("    Surface  : %s" format surf)
    }

    found.headOption.getOrElse {
      println("Void region:")
      println("    Moisture : %.2f" format wet.d)
      println("    Latitude : %s" format lat)
      println("    Altitude : %s" format alt)
      println("    Surface  : %s" format surf)
      throw new RuntimeException()
      Void
    }
  }

  import Latitude._
  import Altitude._
  val Void = LandBiome("Void", Color.Black, Code.` `,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Abyss <=> SuperAlpine
  )

  val Lake = AquaticBiome("Lake", Color.Blue.brighten(0.1f), Code.≈,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> SuperAlpine
  )

  val Ocean = AquaticBiome("Ocean", Color.Blue, Code.≈,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Abyss <=> Oceanic
  )

  val PolarDesert = LandBiome("Polar Desert", Color.White, Code.`.`,
    Subpolar <=> Polar,
    0.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Montane
  )

  val FrozenCrags = LandBiome("Frozen Crags", Color.Purple.dim(1.6f), Code.^,
    Subpolar <=> Polar,
    0.`mm/yr` <=> 250.`mm/yr`,
    Subalpine <=> SuperAlpine
  )

  val AlpineTundra = LandBiome("Alpine Tundra", Color.White.dim(1.1f), Code.▲,
    Tropical <=> Polar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Alpine <=> SuperAlpine
  )

  val ArcticTundra = LandBiome("Arctic Tundra", Color.White.dim(1.5f), Code.☼,
    Subpolar <=> Subpolar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Lowlands <=> Subalpine
  )

  val AntarcticTundra = LandBiome("Antarctic Tundra", Color.White.dim(1.3f), Code.☼,
    Polar <=> Polar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Lowlands <=> Subalpine
  )

  val Glacier = LandBiome("Glacier", Color.Pink, Code.☼,
    Tropical <=> Polar,
    750.`mm/yr` <=> 10000.`mm/yr`,
    Subalpine <=> SuperAlpine
  )

  val BorealGlacier = LandBiome("Glacier", Color.Pink, Code.☼,
    Boreal <=> Boreal,
    750.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> SuperAlpine
  )

  val BarrenCliffs = LandBiome("Barren Cliffs", Color.Purple.dim(1.6f), Code.^,
    Tropical <=> Boreal,
    0.`mm/yr` <=> 25.`mm/yr`,
    Montane <=> SuperAlpine
  )

  //T: [-5, 5]
  val Taiga = LandBiome("Taiga", Color.DarkGreen.dim(1.2f), Code.♣,
    Boreal <=> Boreal,
    250.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Subalpine
  )

  val BorealWetlands = LandBiome("Boreal Wetlands", Color.DarkGreen.dim(1.5f), Code.~,
    Boreal <=> Boreal,
    250.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val BorealGrassland = LandBiome("Boreal Grassland", Color.DarkGreen.dim(1.8f), Code.`»`,
    Boreal <=> Boreal,
    25.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Montane
  )

  val BorealDesert = LandBiome("Boreal Desert", Color.DarkGreen.dim(1.9f), Code.`»`,
    Boreal <=> Boreal,
    0.`mm/yr` <=> 25.`mm/yr`,
    Lowlands <=> Montane
  )

  val TropicalGrassland = LandBiome("Tropical Grassland", Color.Green, Code.*,
    Tropical <=> Tropical,
    250.`mm/yr` <=> 500.`mm/yr`,
    Lowlands <=> Highlands
  )

  val TropicalSavanna = LandBiome("Tropical Savanna", Color.Orange, Code.*,
    Tropical <=> Subtropical,
    500.`mm/yr` <=> 1300.`mm/yr`,
    Lowlands <=> Highlands
  )

  val SubtropicalScrubland = LandBiome("Subtropical Scrubland", Color.Green.dim(1.5f), Code.*,
    Subtropical <=> Subtropical,
    250.`mm/yr` <=> 500.`mm/yr`,
    Lowlands <=> Highlands
  )

  val PeatSwampForest = LandBiome("Peat Swamp Forest", Color.Orange.dim(1.5f), Code.*,
    Tropical <=> Subtropical,
    1300.`mm/yr` <=> 2000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val SubtropicalDeciduousForest = LandBiome("Subtropical Deciduous Forest", Color.Green.dim(1.5f), Code.*,
    Tropical <=> Subtropical,
    1300.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Highlands
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TropicalRainforest = LandBiome("Tropical Rainforest", Color.Green.brighten(0.1f), Code.*,
    Tropical <=> Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> Highlands
  )

  val Mangroves = LandBiome("Mangroves", Color.Brown.brighten(0.1f), Code.~,
    Tropical <=> Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val MontaneRainforest = LandBiome("Montane Rainforest", Color.Green.dim(1.7f), Code.*,
    Subtropical <=> CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Montane <=> Subalpine
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TemperateRainforest = LandBiome("Temperate Rainforest", Color.Green.dim(1.4f), Code.*,
    WarmTemperate <=> CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> Highlands
  )

  val SubtropicalBroadleafForest = LandBiome("Subtropical Broadleaf Forest", Color.DarkGreen.brighten(0.1f), Code.♠,
    Subtropical <=> Subtropical,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Montane
  )

  val TemperateBroadleafForest = LandBiome("Temperate Broadleaf Forest", Color.DarkGreen.brighten(0.1f), Code.♠,
    WarmTemperate <=> CoolTemperate,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Montane
  )

  //http://answers.yahoo.com/question/index?qid=20120201161323AAOwMtY
  val TemperateDeciduousForest = LandBiome("Temperate Deciduous Forest", Color.DarkGreen, Code.♠,
    WarmTemperate <=> CoolTemperate,
    750.`mm/yr` <=> 1500.`mm/yr`,
    Midlands <=> Montane
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/bioconiferous.php
  val TemperateConiferousForest = LandBiome("Temperate Coniferous Forest", Color.DarkGreen.dim(2), Code.♠,
    WarmTemperate <=> CoolTemperate,
    300.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Subalpine
  )

  val TemperateWetlands = LandBiome("Temperate Wetlands", Color.Brown.dim(2), Code.~,
    WarmTemperate <=> CoolTemperate,
    180.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val Steppe = LandBiome("Steppe", Color.Brown.brighten(0.3f), Code.`*`,
    Subtropical <=> CoolTemperate,
    250.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Highlands
  )

  val TemperateScrubland = LandBiome("Temperate Scrubland", Color.Yellow.dim(2), Code.*,
    WarmTemperate <=> CoolTemperate,
    25.`mm/yr` <=> 180.`mm/yr`,
    Lowlands <=> Lowlands
  )

  //approximately 1 in. (.25 cm) of rain falls in dry deserts per year.
  //The latitude range is 15-28° north and south of the equator.
  //http://www.blueplanetbiomes.org/desert_climate_page.htm
  val AridDesert = LandBiome("Arid Desert", Color.Yellow.dim(1.1f), Code.`.`,
    Tropical <=> CoolTemperate,
    0.`mm/yr` <=> 25.`mm/yr`,
    Lowlands <=> Highlands
  )

  //made up
  /*val Badlands = LandBiome("Badlands", Color.Brown, Code.`-`,
    CoolTemperate <=> Boreal,
    0.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Highlands
  )*/

  val XericScrubland = LandBiome("Xeric Scrubland", Color.Yellow.dim(1.5f), Code.`,`,
    Tropical <=> Subtropical,
    25.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Highlands
  )

  val TallGrassland = LandBiome("Tall Temperate Grassland", Color.Green.dim(2f), Code.`»`,
    WarmTemperate <=> CoolTemperate,
    250.`mm/yr` <=> 300.`mm/yr`,
    Lowlands <=> Highlands
  )

  val MontaneGrassland = LandBiome("Montane Grassland", Color.Green.dim(4f), Code.`»`,
    Subtropical <=> Boreal,
    25.`mm/yr` <=> 300.`mm/yr`,
    Montane <=> Alpine
  )

  val ShortGrassland = LandBiome("Short Temperate Grassland", Color.Green.dim(3f), Code.`»`,
    WarmTemperate <=> CoolTemperate,
    25.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Hellscape = LandBiome("Hellscape", Color.Red, Code.x,
    SuperTropical <=> SuperTropical,
    0.`mm/yr` <=> 0.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Infestation = LandBiome("Infestation", Color.Green, Code.`#`,
    SuperTropical <=> SuperTropical,
    0.01.`mm/yr` <=> 2000.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Maelstrom = LandBiome("Maelstrom", Color.Grey, Code.`@`,
    SuperTropical <=> SuperTropical,
    2000.`mm/yr` <=> 7000.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Roil = LandBiome("Infestation", Color.Cyan, Code.`¢`,
    SuperTropical <=> SuperTropical,
    7000.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Highlands
  )

   val All:Vector[Biome] = Reflection.getEnum(Biomes, this.getClass, "Biome", _ == "Void")


 }
