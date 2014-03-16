package com.deweyvm.dogue.world.biomes

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.ColorHarmony
import com.deweyvm.dogue.world._
import com.deweyvm.dogue.{Game, DogueImplicits}
import DogueImplicits._
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Biomes {
  val Void = Biome("Void", BiomeSpec(
    SurfaceType.Void,
    BiomeType.Void,
    Latitude.Void <=> Latitude.Void,
    0.`mm/yr` <=> 10000.`mm/yr`,
    Altitude.Void <=> Altitude.Void
  ))
}

class Biomes(all:Vector[Biome]) {
  val resolver = new BiomeResolver {
    type BiomeInfo = (Rainfall, LatitudinalRegion, AltitudinalRegion, SurfaceType)
    val conflicts = mutable.Map[Set[Biome], Vector[BiomeInfo]]().withDefaultValue(Vector())
    val void = ArrayBuffer[BiomeInfo]()
    override def resolve(wet:Rainfall, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType, bs:Seq[Biome]): Biome = {
      if (bs.length > 1) {
        val s = Set(bs:_*)
        conflicts(s) = conflicts(s) :+ ((wet, lat, alt, surf))
      }

      bs.headOption.getOrElse {
        void += ((wet, lat, alt, surf))
        Biomes.Void
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


  def getBiome(wet:Rainfall, lat:LatitudinalRegion, alt:AltitudinalRegion, surf:SurfaceType):Biome = {
    val found = all.filter { b =>
      b.spec.surface == surf &&
      b.spec.latitude.contains(lat) &&
      b.spec.moisture.contains(wet) &&
      b.spec.altitude.contains(alt)
    }
    resolver.resolve(wet, lat, alt, surf, found)
  }

  val colorMap:Map[Biome,Color] = {
    val map = mutable.Map[Biome,Color]()
    map(Biomes.Void) = Color.Black
    val groups: Map[BiomeType, Vector[Biome]] = all.groupBy(_.spec.`type`)
    for (b <- groups.keys) {
      val group = groups(b)
      val count = group.length
      val colors = ColorHarmony.create(count, b.baseHue, 0.1, 0.0, 0.0, 1, 1, 1f, 0.7f, 0L)
      for (i <- 0 until count) {
        val biome = group(i)
        map(biome) = colors(i)
      }

    }
    map.toMap
  }
}
