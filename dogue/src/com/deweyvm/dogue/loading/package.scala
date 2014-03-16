package com.deweyvm.dogue

import com.deweyvm.dogue.world.{SurfaceType, LatitudinalRegion, AltitudinalRegion}
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.world.biomes.{Biomes, BiomeSpec, Biome, BiomeType}
import com.deweyvm.dogue.common.data.{EitherWriter, Code, DogueRange}
import com.deweyvm.dogue.common.data.algebra.Algebra
import DogueImplicits._

package object loading {

  def filterDuplicates[K](fmtString:String, ts:Map[String,Seq[K]], getName:K=>String):LoadResult[Map[String,K]] = {
    val (dupes, filmap) = ts.values.foldLeft(Vector[String](), Map[String,K]()) { case ((dup, map), v) =>
      v match {
        case b1 +: b2 +: rest =>
          val appended = dup :+ (fmtString format getName(b1))
          (appended, map.updated(getName(b1), b1))
        case b1 +: rest =>
          (dup, map.updated(getName(b1), b1))
      }
    }

    if (dupes.length > 0) {
      EitherWriter(dupes, None)
    } else {
      EitherWriter.unit(filmap)
    }
  }

  def parseRegionMap[TLoader, TRegion, TMap, K](label:String,
                                             regions:Seq[TLoader],
                                             fMax:TLoader => K,
                                             getName:TLoader => String,
                                             getNameRegion:TRegion => String,
                                             regionCtor:(String,DogueRange[K]) => TRegion,
                                             mapCtor:Map[String,TRegion] => TMap,
                                             min:K)(implicit o:Ordering[K]):LoadResult[TMap] = {
    val sorted:Vector[TLoader] = regions.sortBy(fMax).toVector
    val indexed = for (i <- 0 until sorted.length) yield {
      val lower:K = sorted.tryGet(i - 1).map(fMax).getOrElse(min)
      val upper:K = fMax(sorted(i))
      val name = getName(sorted(i))
      regionCtor(name, lower <=> upper)
    }
    val map = makeMap[TRegion,TRegion](indexed, x => x, getNameRegion)
    val typeMap = filterDuplicates[TRegion](label, map, getNameRegion)
    typeMap.map(mapCtor)
  }

  type LoadResult[T] = EitherWriter[Vector[String],T]

  def loadLatitudeManifest(manifest:LatitudeManifest):LoadResult[LatitudeRegionMap] = {
      parseRegionMap[LatitudeLoader, LatitudinalRegion, LatitudeRegionMap, Double]("Duplicate latitude region \"%s\"" , manifest.regions, _.maxRadius, _.name, _.name, LatitudinalRegion, LatitudeRegionMap, 0.0)
  }

  def loadAltitudeManifest(manifest:AltitudeManifest):LoadResult[AltitudeRegionMap] = {
      parseRegionMap[AltitudeLoader, AltitudinalRegion, AltitudeRegionMap, Meters]("Duplicate altitude region \"%s\"" , manifest.altitudes, _.max, _.name, _.name, AltitudinalRegion, AltitudeRegionMap, -10000.m)
  }

  def makeMap[K, T](s:Seq[K], f:K=>T, getName:T=>String):Map[String, Vector[T]] = {
    val baseMap = Map[String,Vector[T]]().withDefaultValue(Vector())
    s.foldLeft(baseMap){ case (map, k) =>
      val t = f(k)
      val name = getName(t)
      map.updated(name, map(name) :+ f(k))
    }
  }

  def loadBiomeTypeManifest(manifest:BiomeTypeManifest):LoadResult[BiomeTypeMap] = {
    def loaderToBiomeType(b:BiomeTypeLoader) = {
      val name = Option(b.name).getOrElse("<unknown>")
      val hue = b.hue
      val code = Code.intToCode(b.code)
      new BiomeType(name, hue, code)
    }
    val map = makeMap[BiomeTypeLoader,BiomeType](manifest.biomeTypes, loaderToBiomeType, _.name)
    val typeMap = filterDuplicates[BiomeType]("Duplicate biome type \"%s\"", map, _.name)
    typeMap.map(BiomeTypeMap)
  }

  def loadSurfaceManifest(manifest:SurfaceTypeManifest):LoadResult[SurfaceTypeMap] = {
    def loaderToSurfaceType(l:SurfaceTypeLoader) = {
      val name = Option(l.name).getOrElse("<unknown>")
      val isWater = l.isWater
      SurfaceType(name, isWater)
    }

    val map = makeMap[SurfaceTypeLoader, SurfaceType](manifest.types, loaderToSurfaceType, _.name)
    val typeMap = filterDuplicates[SurfaceType]("Duplicate surface type \"%s\"", map, _.name)
    typeMap.map(SurfaceTypeMap)
  }

  def loadBiomes(biomeManifest:BiomeManifest,
                 latMap:LatitudeRegionMap,
                 altMap:AltitudeRegionMap,
                 biomeMap:BiomeTypeMap,
                 surfaceMap:SurfaceTypeMap):LoadResult[Biomes] = {
    val b: Seq[LoadResult[Biome]] = biomeManifest.biomes.map{loadBiome(_, latMap, altMap, biomeMap, surfaceMap)}
    println("sequencing")
    b foreach {
      case EitherWriter(log, None) => println(log)
      case _ =>
        ()
    }
    val s: LoadResult[Seq[Biome]] = EitherWriter.sequence(b)
    for {
      bs <- s
    } yield {
      new Biomes(bs.toVector)
    }
  }

  def loadBiome(loader:BiomeLoader,
                latMap:LatitudeRegionMap,
                altMap:AltitudeRegionMap,
                biomeMap:BiomeTypeMap,
                surfaceMap:SurfaceTypeMap):LoadResult[Biome] = {
    import Algebra._
    def getAltitude(s:String) = {
      ("Altitude \"%s\" not found" format s) ~> (altMap.map.contains(s), altMap.map(s))
    }

    def getLatitude(s:String) = {
      ("Latitude \"%s\" not found" format s) ~> (latMap.map.contains(s), latMap.map(s))
    }

    def makeRange[T](label:String, min:T, max:T)(implicit o:Ordering[T]) = {
      ("Range %s is invalid. Requires min <= max. got %s <=> %s" format (label, min, max)) ~> (o.lteq(min, max), min <=> max)
    }
    def nonEmpty(label:String, s:String) = {
      ("Value \"%s\" is empty" format label) ~> (s != null && s.replace(" ", "").length > 0, s)
    }

    def getBiomeType(t:String) = {
      ("Biome type \"%s\" does not exist" format t) ~> (biomeMap.map.contains(t), biomeMap.map(t))
    }

    def getSurfaceType(t:String) = {
      ("Surface type \"%s\" does not exist" format t) ~> (surfaceMap.map.contains(t), surfaceMap.map(t))
    }

    for {
      name <- nonEmpty("name", loader.name)
      biomeType <- getBiomeType(loader.biomeType)
      surfaceType <- getSurfaceType(loader.surfaceType)
      minLat <- getLatitude(loader.minLat)
      maxLat <- getLatitude(loader.maxLat)
      latitude <- makeRange("latitude", minLat, maxLat)
      rain <- makeRange("rainfall", loader.minRain.`mm/yr`, loader.maxRain.`mm/yr`)
      minAlt <- getAltitude(loader.minAlt)
      maxAlt <- getAltitude(loader.maxAlt)
      altitude <- makeRange("altitude", minAlt, maxAlt)
    } yield {
      Biome(name, BiomeSpec(surfaceType, biomeType, latitude, rain, altitude))
    }
  }

  //def loadBiomes
}
