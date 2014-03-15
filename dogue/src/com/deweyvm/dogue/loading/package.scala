package com.deweyvm.dogue

import com.deweyvm.dogue.world.{Surface, LatitudinalRegion, AltitudinalRegion}
import com.deweyvm.dogue.common.data.serialization.{Verifier, Writable}
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.world.biomes.{BiomeSpec, Biomes, Biome, BiomeType}
import com.deweyvm.dogue.common.data.{EitherWriter, Code, DogueRange}
import com.deweyvm.dogue.common.data.algebra.Algebra
import DogueImplicits._
package object loading {


  implicit def altitude2Writable(alt:AltitudinalRegion) = new Writable[AltitudinalRegion] {
    def write = alt.toString
  }

  implicit def rainfall2Writable(r:Rainfall) = new Writable[Rainfall] {
    def write = r.d.toString
  }

  implicit def biomeType2Writable(t:BiomeType) = new Writable[BiomeType] {
    def write = t.name
  }

  def filterDuplicates[K](fmtString:String, ts:Map[String,Seq[K]], getName:K=>String):Either[String, Map[String,K]] = {
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
      Left(dupes.mkString("\n"))
    } else {
      Right(filmap)
    }
  }

  def parseRegionMap[TLoader, TRegion, TMap](
                                              regions:Seq[TLoader],
                                              fMax:TLoader => Double,
                                              getName:TLoader => String,
                                              getNameRegion:TRegion => String,
                                              regionCtor:(String,DogueRange[Double]) => TRegion,
                                              mapCtor:Map[String,TRegion] => TMap,
                                              min:Double):Either[String,TMap] = {
    val sorted:Vector[TLoader] = regions.sortBy(fMax).toVector
    val indexed = for (i <- 0 until sorted.length) yield {
      val lower:Double = sorted.tryGet(i - 1).map(fMax).getOrElse(min)
      val upper:Double = fMax(sorted(i))
      val name = getName(sorted(i))
      regionCtor(name, lower <=> upper)
    }
    val map = makeMap[TRegion,TRegion](indexed, x => x, getNameRegion)
    val typeMap = filterDuplicates[TRegion]("Duplicate latitude region \"%s\"", map, getNameRegion)
    typeMap.right.map(mapCtor)
  }

  implicit def latitudeManifest2Verifier(manifest:LatitudeManifest) = new Verifier[LatitudeMap] {
    def verify = {
      parseRegionMap[LatitudeLoader, LatitudinalRegion, LatitudeMap](manifest.regions, _.maxRadius, _.name, _.name, LatitudinalRegion, LatitudeMap, 0.0)
      /*val sorted:Vector[LatitudeLoader] = manifest.regions.sortBy{_.maxRadius}.toVector
      val indexed = for (i <- 0 until sorted.length) yield {
        val lower:Double = sorted.tryGet(i - 1).map{_.maxRadius}.getOrElse(0.0)
        val upper:Double = sorted(i).maxRadius
        val name = sorted(i).name
        LatitudinalRegion(name, lower <=> upper)
      }
      val map = makeMap[LatitudinalRegion,LatitudinalRegion](indexed, x => x, _.name)
      val typeMap = filterDuplicates[LatitudinalRegion]("Duplicate latitude region \"%s\"", map, _.name)
      typeMap.right.map(LatitudeMap)*/
    }
  }

  implicit def altitudeManifest2Verifier(manifest:AltitudeManifest) = new Verifier[AltitudeMap] {
    def verify = {
      parseRegionMap[AltitudeLoader, AltitudinalRegion, AltitudeMap](manifest.altitudes, _.max, _.name, _.name, AltitudinalRegion, AltitudeMap, -10000)
      /*val sorted:Vector[AltitudeLoader] = manifest.altitudes.sortBy{_.max}.toVector
      val indexed = for (i <- 0 until sorted.length) yield {
        val lower:Double = sorted.tryGet(i - 1).map{_.max}.getOrElse(0.0)
        val upper:Double = sorted(i).max
        val name = sorted(i).name
        AltitudinalRegion(name, lower.m <=> upper.m)
      }
      val map = makeMap[AltitudinalRegion,AltitudinalRegion](indexed, x => x, _.name)
      val typeMap = filterDuplicates[AltitudinalRegion]("Duplicate altitude region \"%s\"", map, _.name)
      typeMap.right.map(AltitudeMap)*/
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
      val name = Option(b.name).getOrElse("<unknown>")
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

  implicit def surfaceTypeMap2Verifier(manifest:SurfaceTypeManifest) = new Verifier[SurfaceTypeMap] {
    def surfaceLoaderToType(l:SurfaceTypeLoader) = {
      val name = Option(l.name).getOrElse("<unknown>")
      val isWater
    }
  }

  implicit def biomeLoader2Verifier(loader:BiomeLoader) = new Verifier[Biome] {
    import Algebra._
    val latMap:LatitudeMap = ???
    val altMap:AltitudeMap = ???
    val biomeMap:BiomeTypeMap = ???
    val surfaceMap:SurfaceTypeMap = ???

    def getAltitude(s:String) = {
      ("Altitude \"%s\" not found" format s) ~> (altMap.map.contains(s), altMap.map(s))
    }

    def getLatitude(s:String) = {
      ("Latitude \"%s\" not found" format s) ~> (latMap.map.contains(s), latMap.map(s))
    }

    def makeRange[T](label:String, min:T, max:T)(implicit o:Ordering[T]) = {
      ("Range " + label + " is invalid. Requires min <= max.") ~> (o.gt(min, max), min <=> max)
    }
    def nonEmpty(label:String, s:String) = {
      ("Value \"%s\" is empty" format label) ~> (s == null || s.replace(" ", "").length == 1, s)
    }

    def getBiomeType(t:String) = {
      ("Biome type \"%s\" does not exist" format t) ~> (biomeMap.map.contains(t), biomeMap.map(t))
    }

    def getSurfaceType(t:String) = {
      ("Surface type \"%s\" does not exist" format t) ~> (surfaceMap.map.contains(t), surfaceMap.map(t))
    }

    def verify = {

      val s: EitherWriter[Vector[String], Biome] = for {
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
        biome = Biome(name, BiomeSpec(surfaceType, biomeType, latitude, rain, altitude))
      } yield {
        biome
      }

      if (s.failed(_.length == 0)) {
        s foreach {println(_)}
        throw new RuntimeException
      } else {
        //fixme
        s.value.right.get
      }

      Right(Biomes.Void)
    }
  }
}
