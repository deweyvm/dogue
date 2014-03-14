package com.deweyvm

import com.deweyvm.dogue.world.{AltitudinalRegion, LatitudinalRegion}
import com.deweyvm.dogue.common.data.serialization.{Verifier, Writable}
import com.deweyvm.dogue.world.biomes.{Biomes, BiomeType, Biome}
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.loading._
import com.deweyvm.dogue.common.data.Code
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.world.biomes.Biome
import com.deweyvm.dogue.common.CommonImplicits.Rainfall
import com.deweyvm.dogue.world.LatitudinalRegion
import com.deweyvm.dogue.loading.BiomeTypeMap
import com.deweyvm.dogue.world.AltitudinalRegion
import scala.collection.immutable.IndexedSeq

package object dogue {
  object DogueImplicits {
    implicit val latitudeOrdering = new Ordering[LatitudinalRegion] {
      def compare(m1:LatitudinalRegion, m2:LatitudinalRegion) =
        m1.range.max.compare(m2.range.max)

    }

    implicit val altitudeOrdering = new Ordering[AltitudinalRegion] {
      def compare(m1:AltitudinalRegion, m2:AltitudinalRegion) =
        m1.range.max.d.compare(m2.range.max.d)

    }

    implicit def altitude2Writable(alt:AltitudinalRegion) = new Writable[AltitudinalRegion] {
      def write = alt.toString
    }

    implicit def rainfall2Writable(r:Rainfall) = new Writable[Rainfall] {
      def write = r.d.toString
    }

    implicit def biomeType2Writable(t:BiomeType) = new Writable[BiomeType] {
      def write = t.name
    }


    implicit def latitudeManifest2Verifier(manifest:LatitudeManifest) = new Verifier[LatitudeMap] {
      def verify = {
        val sorted:Vector[LatitudeLoader] = manifest.regions.sortBy{_.maxRadius}.toVector
        val indexed = for (i <- 0 until sorted.length) yield {
          val lower:Double = sorted.tryGet(i - 1).map{_.maxRadius}.getOrElse(0.0)
          val upper:Double = sorted(i).maxRadius
          val name = sorted(i).name
          LatitudinalRegion(name, lower <=> upper)
        }
        val map = makeMap[LatitudinalRegion,LatitudinalRegion](indexed, x => x, _.name)
        val typeMap = filterDuplicates[LatitudinalRegion]("Duplicate latitude region \"%s\"", map, _.name)
        typeMap.right.map(LatitudeMap)
      }
    }

    implicit def biomeLoader2Verifier(loader:BiomeLoader) = new Verifier[Biome] {
      def verify:Either[String,Biome] = {
        //val minLat = loader.minLat.read[LatitudinalRegion]
        Right(Biomes.Void)
      }
    }

    def makeMap[K, T](s:Seq[K], f:K=>T, getName:T=>String):Map[String, Vector[T]] = {
      val baseMap = Map[String,Vector[T]]().withDefaultValue(Vector())
      s.foldLeft(baseMap){ case (map, k) =>
        val t = f(k)
        val name = getName(t)
        map.updated(name, map(name) :+ f(k))
      }
    }

    implicit def biomeTypeMap2Verifier(manifest:BiomeTypeManifest) = new Verifier[BiomeTypeMap] {
      def loaderToBiomeType(b:BiomeTypeLoader) = {
        val name = nonEmpty(b.name).getOrElse("<unknown>")
        val hue = b.hue
        val code = Code.intToCode(b.code)
        new BiomeType(name, hue, code)
      }
      def verify:Either[String,BiomeTypeMap] = {
        val map = makeMap[BiomeTypeLoader,BiomeType](manifest.biomeTypes, loaderToBiomeType, _.name)

        val typeMap = filterDuplicates[BiomeType]("Duplicate biome type \"%s\"", map, _.name)

        typeMap.right.map(BiomeTypeMap)
      }
    }

  }
}
