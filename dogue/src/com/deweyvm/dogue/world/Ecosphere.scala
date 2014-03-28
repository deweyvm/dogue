package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.data.{Array2dView, Array2d}
import com.deweyvm.gleany.data._
import com.deweyvm.dogue.world.AtmosphereConstants._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.loading._
import com.deweyvm.dogue.DogueImplicits
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.dogue.world.biomes.{Biomes, Biome}
import com.deweyvm.dogue.loading.AltitudeRegionMap
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.logging.Log
import DogueImplicits._
object Ecosphere {
  def buildEcosphere(worldParams:WorldParams,
                     latitude:LatitudeMap,
                     noise:Array2d[Double],
                     surface:SurfaceMap,
                     wind:StaticWindMap,
                     moisture:MoistureMap,
                     biomeMap:BiomeMap,
                     surfaceRegions:SurfaceTypeMap,
                     latRegions:LatitudeRegionMap,
                     altRegions:AltitudeRegionMap,
                     timeStrings:Vector[String]):Ecosphere = new Ecosphere {
    outer =>
    biomeMap.biomes.resolver.printConflicts()

    override val cols = worldParams.size
    override val rows = worldParams.size

    override def getTimeStrings:Vector[String] = {
      timeStrings
    }

    private val atmosphereMap:Array2dView[Pressure] = {
      surface.heightMap.viewMap{case (i, j, h) =>
        val f = (h < 0.m) select (waterPressure _, airPressure _)
        f(h)
      }
    }

    override def update = {
      if (Controls.Insert.justPressed) {
        val (t, r) = Timer.timer(() => {
          val b = for {
            biomes <- Loads.loadBiomes(latRegions, altRegions, surfaceRegions)
          } yield {
            biomes.resolver.printConflicts()
            val biomeMap = new BiomeMap(moisture, surface, latitude, altRegions, biomes)
            buildEcosphere(worldParams, latitude, noise, surface, wind, moisture, biomeMap, surfaceRegions, latRegions, altRegions, timeStrings)
          }
          b.either match {
            case Left(err) =>
              Log.error("Failed to refresh biome map")
              err foreach Log.error
              this
            case Right(result) =>
              Log.warn("Biome map successfully reloaded")
              result
          }
        })
        Log.warn("Took %dms" format (t/1000000L))
        r
      } else {
        this
      }
    }

    override def getWind(i:Int, j:Int):Arrow = wind.arrows.get(i, j)

    override def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
      getElevationParts(i, j)
    }

    override def getLatitude(i:Int, j:Int):LatitudinalRegion = {
      latitude.regions.get(i, j)
    }

    override def getBiome(i:Int, j:Int):(Biome,Color) = {
      val biome = biomeMap.biomeArray.get(i, j)
      (biome, biomeMap.biomes.colorMap(biome))
    }

    override def getPressure(i:Int, j:Int):Pressure = {
      atmosphereMap.get(i, j)
    }


    override def getMoisture(i:Int, j:Int):Rainfall = {
      moisture.get(i, j)
    }

    private def getElevationParts(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
      val t = surface.landMap.get(i, j)
      val h = surface.heightMap.get(i, j)
      val altitude = altRegions.fromHeight(h)
      (t, h, altitude)
    }
  }
}

trait Ecosphere {
  val cols:Int
  val rows:Int
  def getLatitude(i:Int, j:Int):LatitudinalRegion
  def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = (SurfaceType.Void, 0.m, Altitude.Void)
  def getWind(i:Int, j:Int):Arrow = Arrow(1.dup, 1)
  def getBiome(i:Int, j:Int):(Biome,Color) = (Biomes.Void,Color.Black)
  def getPressure(i:Int, j:Int):Pressure = 1.atm
  def getMoisture(i:Int, j:Int):Rainfall = 0.`mm/yr`
  def getTemperature(i:Int, j:Int):Celcius = 20.C
  def getTimeStrings:Vector[String]
  def update:Ecosphere
}
