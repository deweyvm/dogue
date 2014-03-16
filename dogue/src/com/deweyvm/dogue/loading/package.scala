package com.deweyvm.dogue

import com.deweyvm.dogue.world.{SurfaceType, LatitudinalRegion, AltitudinalRegion}
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.world.biomes.{Biomes, BiomeSpec, Biome, BiomeType}
import com.deweyvm.dogue.common.data.{Writer, Code, DogueRange}
import com.deweyvm.dogue.common.data.algebra.Algebra
import DogueImplicits._
import com.deweyvm.dogue.common.logging.Log

package object loading {
  type LoadResult[T] = Writer[Vector[String],T]

  implicit class LoadResultUtil[A](l:LoadResult[A]){
    def getOrCrash = l.getOrCrash(_.length == 0, _ foreach Log.warn)
    def toEither = l.toEither(_.length == 0)
  }

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
      Writer(dupes, None)
    } else {
      Writer.unit(filmap)
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


  def loadLatitudeManifest(manifest:LoadResult[LatitudeManifest]):LoadResult[LatitudeRegionMap] = {
      for {
        m <- manifest
        map <- parseRegionMap[LatitudeLoader, LatitudinalRegion, LatitudeRegionMap, Double]("Duplicate latitude region \"%s\"" , m.regions, _.maxRadius, _.name, _.name, LatitudinalRegion, LatitudeRegionMap, 0.0)
      } yield {
        map
      }
  }

  def loadAltitudeManifest(manifest:LoadResult[AltitudeManifest]):LoadResult[AltitudeRegionMap] = {
    for {
      m <- manifest
      map <- parseRegionMap[AltitudeLoader, AltitudinalRegion, AltitudeRegionMap, Meters]("Duplicate altitude region \"%s\"" , m.altitudes, _.max, _.name, _.name, AltitudinalRegion, AltitudeRegionMap, -10000.m)
    } yield {
      map
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

  def loadBiomeTypeManifest(manifest:LoadResult[BiomeTypeManifest]):LoadResult[BiomeTypeMap] = {
    def loaderToBiomeType(b:BiomeTypeLoader) = {
      val name = Option(b.name).getOrElse("<unknown>")
      val hue = b.hue
      val code = Code.intToCode(b.code)
      new BiomeType(name, hue, code)
    }
    for {
      m <- manifest
      map = makeMap[BiomeTypeLoader,BiomeType](m.biomeTypes, loaderToBiomeType, _.name)
      filtered <- filterDuplicates[BiomeType]("Duplicate biome type \"%s\"", map, _.name)
    } yield {
      BiomeTypeMap(filtered)
    }
  }

  def loadSurfaceManifest(manifest:LoadResult[SurfaceTypeManifest]):LoadResult[SurfaceTypeMap] = {
    def loaderToSurfaceType(l:SurfaceTypeLoader) = {
      val name = Option(l.name).getOrElse("<unknown>")
      val isWater = l.isWater
      SurfaceType(name, isWater)
    }
    for {
      m <- manifest
      map = makeMap[SurfaceTypeLoader, SurfaceType](m.types, loaderToSurfaceType, _.name)
      filtered <- filterDuplicates[SurfaceType]("Duplicate surface type \"%s\"", map, _.name)
    } yield {
      SurfaceTypeMap(filtered)
    }

  }

  def loadBiomes(biomeManifest:LoadResult[BiomeManifest],
                 latMap:LatitudeRegionMap,
                 altMap:AltitudeRegionMap,
                 biomeMap:BiomeTypeMap,
                 surfaceMap:SurfaceTypeMap):LoadResult[Biomes] = {
    for {
      m <- biomeManifest
      b = m.biomes.map{loadBiome(_, latMap, altMap, biomeMap, surfaceMap)}
      bs <- Writer.sequence(b)
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
      ("Altitude \"%s\" not found" format s) ~|> (altMap.map.contains(s), altMap.map(s))
    }

    def getLatitude(s:String) = {
      ("Latitude \"%s\" not found" format s) ~|> (latMap.map.contains(s), latMap.map(s))
    }

    def makeRange[T](label:String, min:T, max:T)(implicit o:Ordering[T]) = {
      ("Range %s is invalid. Requires min <= max. got %s <=> %s" format (label, min, max)) ~|> (o.lteq(min, max), min <=> max)
    }
    def nonEmpty(label:String, s:String) = {
      ("Value \"%s\" is empty" format label) ~|> (s != null && s.replace(" ", "").length > 0, s)
    }

    def getBiomeType(t:String) = {
      ("Biome type \"%s\" does not exist" format t) ~|> (biomeMap.map.contains(t), biomeMap.map(t))
    }

    def getSurfaceType(t:String) = {
      ("Surface type \"%s\" does not exist" format t) ~|> (surfaceMap.map.contains(t), surfaceMap.map(t))
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

  object Loads {
    def loadRegionMaps:LoadResult[(AltitudeRegionMap, LatitudeRegionMap, SurfaceTypeMap)] = {
      for {
        altRegions <- loading.loadAltitudeManifest(Loader.fromFile[AltitudeManifest]("data/world/altitudes.manifest"))
        latRegions <- loading.loadLatitudeManifest(Loader.fromFile[LatitudeManifest]("data/world/latitudes.manifest"))
        surfaceRegions <- loading.loadSurfaceManifest(Loader.fromFile[SurfaceTypeManifest]("data/world/surfacetypes.manifest"))
      } yield {
        (altRegions, latRegions, surfaceRegions)
      }

    }
    def loadBiomes(latRegions:LatitudeRegionMap,
                   altRegions:AltitudeRegionMap,
                   surfaceRegions:SurfaceTypeMap):LoadResult[Biomes] = {
      for {
        biomeTypes <- loading.loadBiomeTypeManifest(Loader.fromFile[BiomeTypeManifest]("data/biomes/biometypes.manifest"))
        biomes <- loading.loadBiomes(Loader.fromFile[BiomeManifest]("data/biomes/biomes.manifest"), latRegions, altRegions, biomeTypes, surfaceRegions)
      } yield {
        biomes
      }
    }
  }
}
