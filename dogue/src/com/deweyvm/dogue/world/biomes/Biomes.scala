package com.deweyvm.dogue.world.biomes

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{ColorHarmony, DogueRange}
import com.deweyvm.dogue.world._
import com.deweyvm.dogue.common.reflect.Reflection
import com.deweyvm.dogue.DogueImplicits
import DogueImplicits._
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object Biomes {
  val resolver = new BiomeResolver {
    type BiomeInfo = (Rainfall,LatitudinalRegion, AltitudinalRegion, SurfaceType)
    val conflicts = mutable.Map[Set[Biome], Vector[BiomeInfo]]().withDefaultValue(Vector())
    val void = ArrayBuffer[BiomeInfo]()
    override def resolve(wet:Rainfall, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType, bs:Seq[Biome]): Biome = {
      if (bs.length > 1) {
        val s = Set(bs:_*)
        conflicts(s) = conflicts(s) :+ ((wet, lat, alt, surf))
      }

      bs.headOption.getOrElse {
        void += ((wet, lat, alt, surf))
        Void
      }
    }

    override def printConflicts() {
      def printThing(header:String, biomes:Vector[BiomeInfo]) {
        val rainfall = biomes.map {_._1.d}
        val rMin = rainfall.min
        val rMax = rainfall.max
        val lats = biomes.map{_._2}
        val lMin = lats.min
        val lMax = lats.max
        val alts = biomes.map{_._3}
        val aMin = alts.min
        val aMax = alts.max
        val stypes = Set(biomes.map{_._4} : _*)
        println(header)
        println("    Rainfall: %.2f <=> %.2f" format (rMin, rMax))
        println("    Latitude: %s <=> %s" format (lMin, lMax))
        println("    Altitude: %s <=> %s" format (aMin, aMax))
        println("    Surface :(%s)" format stypes.mkString(","))
      }
      conflicts.keys foreach { s =>
        val biomes = conflicts(s)
        printThing("Conflict: (%s)" format s.mkString(","), biomes)
      }
      void foreach {v =>
        println("Void region:")
        println("    Moisture : %.2f" format v._1.d)
        println("    Latitude : %s" format v._2)
        println("    Altitude : %s" format v._3)
        println("    Surface  : %s" format v._4)
      }
    }
  }
  def LandBiome(name:String,
                `type`:BiomeType,
                region:DogueRange[LatitudinalRegion],
                moisture:DogueRange[Rainfall],
                altitude:DogueRange[AltitudinalRegion]) = {
    val spec = BiomeSpec(Surface.Land, `type`, region, moisture, altitude)
    Biome(name, spec)
  }

  def AquaticBiome(name:String,
                   `type`:BiomeType,
                   region:DogueRange[LatitudinalRegion],
                   moisture:DogueRange[Rainfall],
                   altitude:DogueRange[AltitudinalRegion]) = {
    val spec = BiomeSpec(Surface.Water, `type`, region, moisture, altitude)
    Biome(name, spec)
  }

  def getBiome(wet:Rainfall, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType):Biome = {
    val found = All.filter { b =>
      b.spec.surface == surf &&
      b.spec.region.contains(lat) &&
      b.spec.moisture.contains(wet) &&
      b.spec.altitude.contains(alt)
    }
    resolver.resolve(wet, lat, alt, surf, found)
  }

  import Latitude._
  import Altitude._
  val Void = LandBiome("Void",
    BiomeType.Special,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Abyss <=> SuperAlpine
  )

  val Lake = AquaticBiome("Lake",
    BiomeType.Aquatic,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> SuperAlpine
  )

  val Ocean = AquaticBiome("Ocean",
    BiomeType.Aquatic,
    SuperTropical <=> Polar,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Abyss <=> Oceanic
  )

  val PolarDesert = LandBiome("Polar Desert",
    BiomeType.Desert,
    Subpolar <=> Polar,
    0.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Montane
  )

  val FrozenCrags = LandBiome("Frozen Crags",
    BiomeType.Desert,
    Subpolar <=> Polar,
    0.`mm/yr` <=> 250.`mm/yr`,
    Subalpine <=> SuperAlpine
  )

  val AlpineTundra = LandBiome("Alpine Tundra",
    BiomeType.Grassland,
    Tropical <=> Polar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Alpine <=> SuperAlpine
  )

  val ArcticTundra = LandBiome("Arctic Tundra",
    BiomeType.Grassland,
    Subpolar <=> Subpolar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Lowlands <=> Subalpine
  )

  val AntarcticTundra = LandBiome("Antarctic Tundra",
    BiomeType.Grassland,
    Polar <=> Polar,
    250.`mm/yr` <=> 750.`mm/yr`,
    Lowlands <=> Subalpine
  )

  val Glacier = LandBiome("Glacier",
    BiomeType.Desert,
    Tropical <=> Polar,
    750.`mm/yr` <=> 10000.`mm/yr`,
    Subalpine <=> SuperAlpine
  )

  val BorealGlacier = LandBiome("Glacier",
    BiomeType.Desert,
    Boreal <=> Boreal,
    750.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> SuperAlpine
  )

  val BarrenCliffs = LandBiome("Barren Cliffs",
    BiomeType.Desert,
    Tropical <=> Boreal,
    0.`mm/yr` <=> 25.`mm/yr`,
    Subalpine <=> SuperAlpine
  )

  //T: [-5, 5]
  val Taiga = LandBiome("Taiga",
    BiomeType.Forest,
    Boreal <=> Boreal,
    250.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Subalpine
  )

  val BorealWetlands = LandBiome("Boreal Wetlands",
    BiomeType.Wetlands,
    Boreal <=> Boreal,
    250.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val BorealGrassland = LandBiome("Boreal Grassland",
    BiomeType.Grassland,
    Boreal <=> Boreal,
    25.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Montane
  )

  val BorealDesert = LandBiome("Boreal Desert",
    BiomeType.Desert,
    Boreal <=> Boreal,
    0.`mm/yr` <=> 25.`mm/yr`,
    Lowlands <=> Montane
  )

  val TropicalGrassland = LandBiome("Tropical Grassland",
    BiomeType.Grassland,
    Tropical <=> Tropical,
    250.`mm/yr` <=> 500.`mm/yr`,
    Lowlands <=> Highlands
  )

  val SubtropicalScrubland = LandBiome("Subtropical Scrubland",
    BiomeType.Grassland,
    Subtropical <=> Subtropical,
    250.`mm/yr` <=> 500.`mm/yr`,
    Lowlands <=> Highlands
  )

  val PeatSwampForest = LandBiome("Peat Swamp Forest",
    BiomeType.Wetlands,
    Tropical <=> Subtropical,
    1300.`mm/yr` <=> 2000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val SubtropicalConiferousForest = LandBiome("Subtropical Coniferous Forest",
    BiomeType.Forest,
    Subtropical <=> Subtropical,
    300.`mm/yr` <=> 1100.`mm/yr`,
    Montane <=> Montane
  )

  val SubtropicalBroadleafForest = LandBiome("Subtropical Broadleaf Forest",
    BiomeType.Forest,
    Subtropical <=> Subtropical,
    1100.`mm/yr` <=> 1500.`mm/yr`,
    Midlands <=> Montane
  )

  val SubtropicalDeciduousForest = LandBiome("Subtropical Deciduous Forest",
    BiomeType.Forest,
    Subtropical <=> Subtropical,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Montane
  )

  val TropicalSavanna = LandBiome("Tropical Savanna",
    BiomeType.Grassland,
    Tropical <=> Subtropical,
    500.`mm/yr` <=> 1100.`mm/yr`,
    Lowlands <=> Highlands
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TropicalRainforest = LandBiome("Tropical Rainforest",
    BiomeType.Forest,
    Tropical <=> Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> Highlands
  )

  val TropicalDeciduousForest = LandBiome("Tropical Deciduous Forest",
    BiomeType.Forest,
    Tropical <=> Tropical,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Highlands
  )

  val TropicalBroadleafForest = LandBiome("Tropical Broadleaf Forest",
    BiomeType.Forest,
    Tropical <=> Tropical,
    1300.`mm/yr` <=> 1500.`mm/yr`,
    Midlands <=> Highlands
  )

  val TropicalConiferousForest = LandBiome("Tropical Coniferous Forest",
    BiomeType.Forest,
    Tropical <=> Tropical,
    1100.`mm/yr` <=> 1300.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Mangroves = LandBiome("Mangroves",
    BiomeType.Wetlands,
    Tropical <=> Subtropical,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val MontaneRainforest = LandBiome("Montane Rainforest",
    BiomeType.Forest,
    Subtropical <=> CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Montane <=> Subalpine
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/biorainforest.php
  val TemperateRainforest = LandBiome("Temperate Rainforest",
    BiomeType.Forest,
    WarmTemperate <=> CoolTemperate,
    2000.`mm/yr` <=> 10000.`mm/yr`,
    Midlands <=> Highlands
  )

  val TemperateBroadleafForest = LandBiome("Temperate Broadleaf Forest",
    BiomeType.Forest,
    WarmTemperate <=> CoolTemperate,
    1500.`mm/yr` <=> 2000.`mm/yr`,
    Midlands <=> Montane
  )

  //http://answers.yahoo.com/question/index?qid=20120201161323AAOwMtY
  val TemperateDeciduousForest = LandBiome("Temperate Deciduous Forest",
    BiomeType.Forest,
    WarmTemperate <=> CoolTemperate,
    750.`mm/yr` <=> 1500.`mm/yr`,
    Midlands <=> Montane
  )

  //http://earthobservatory.nasa.gov/Experiments/Biome/bioconiferous.php
  val TemperateConiferousForest = LandBiome("Temperate Coniferous Forest",
    BiomeType.Forest,
    WarmTemperate <=> CoolTemperate,
    300.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Subalpine
  )

  /*val Steppe = LandBiome("Steppe",
    BiomeType.Grassland,
    Subtropical <=> CoolTemperate,
    250.`mm/yr` <=> 750.`mm/yr`,
    Midlands <=> Highlands
  )
*/

  //approximately 1 in. (.25 cm) of rain falls in dry deserts per year.
  //The latitude range is 15-28° north and south of the equator.
  //http://www.blueplanetbiomes.org/desert_climate_page.htm
  val AridDesert = LandBiome("Arid Desert",
    BiomeType.Desert,
    Tropical <=> CoolTemperate,
    0.`mm/yr` <=> 25.`mm/yr`,
    Lowlands <=> Montane
  )

  val XericScrubland = LandBiome("Xeric Scrubland",
    BiomeType.Grassland,
    Tropical <=> Subtropical,
    25.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Highlands
  )

  val MontaneGrassland = LandBiome("Montane Grassland",
    BiomeType.Grassland,
    Subtropical <=> Boreal,
    25.`mm/yr` <=> 300.`mm/yr`,
    Montane <=> Alpine
  )

  val TallGrassland = LandBiome("Tall Temperate Grassland",
    BiomeType.Grassland,
    WarmTemperate <=> CoolTemperate,
    250.`mm/yr` <=> 300.`mm/yr`,
    Lowlands <=> Highlands
  )

  val ShortGrassland = LandBiome("Short Temperate Grassland",
    BiomeType.Grassland,
    WarmTemperate <=> CoolTemperate,
    80.`mm/yr` <=> 250.`mm/yr`,
    Lowlands <=> Highlands
  )


  val TemperateWetlands = LandBiome("Temperate Wetlands",
    BiomeType.Wetlands,
    WarmTemperate <=> CoolTemperate,
    300.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Lowlands
  )

  val TemperateScrubland = LandBiome("Temperate Scrubland",
    BiomeType.Grassland,
    WarmTemperate <=> CoolTemperate,
    25.`mm/yr` <=> 80.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Hellscape = LandBiome("Hellscape",
    BiomeType.Desert,
    SuperTropical <=> SuperTropical,
    0.`mm/yr` <=> 0.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Infestation = LandBiome("Infestation",
    BiomeType.Wetlands,
    SuperTropical <=> SuperTropical,
    0.01.`mm/yr` <=> 2000.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Maelstrom = LandBiome("Maelstrom",
    BiomeType.Special,
    SuperTropical <=> SuperTropical,
    2000.`mm/yr` <=> 7000.`mm/yr`,
    Lowlands <=> Highlands
  )

  val Roil = LandBiome("Infestation",
    BiomeType.Special,
    SuperTropical <=> SuperTropical,
    7000.`mm/yr` <=> 10000.`mm/yr`,
    Lowlands <=> Highlands
  )

  val All:Vector[Biome] = Reflection.getEnum(Biomes, this.getClass, "Biome", _ == "Void")

  val colorMap:Map[Biome,Color] = {
    val map = mutable.Map[Biome,Color]()
    map(Void) = Color.Black
    val groups: Map[BiomeType, Vector[Biome]] = All.groupBy(_.spec.`type`)
    for (b <- groups.keys) {
      val group = groups(b)
      val count = group.length
      val colors = ColorHarmony.create(count, b.baseHue, 0.1, 0.0, 0.0, 1, 1, 1f, 0.7f, 0L)
      for (i <- 0 until count) {
        val biome = group(i)
        map(biome) = colors(i)//b.baseColor.brighten(i/(2*count.toFloat))
      }

    }
    map.toMap
  }
}
