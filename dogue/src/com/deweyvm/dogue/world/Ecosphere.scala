package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.data.{Array2dView, Array2d}
import com.deweyvm.gleany.data._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.world.AtmosphereConstants._
import com.deweyvm.dogue.common.procgen.Arrow
import java.util.Random
import com.deweyvm.dogue.world.biomes.{Biomes, Biome}
import com.deweyvm.dogue.input.Controls

object Ecosphere {
  private def buildEcosphere(worldParams:WorldParams,
                             latitude:LatitudeMap,
                             noise:Array2d[Double],
                             surface:SurfaceMap,
                             wind:StaticWindMap,
                             moisture:MoistureMap,
                             biome:BiomeMap,
                             timeStrings:Vector[String]):Ecosphere = new Ecosphere {
    outer =>

    override val cols = worldParams.size
    override val rows = worldParams.size

    override def getTimeStrings:Vector[String] = {
      timeStrings
    }

    private val atmosphereMap:Array2dView[Pressure] = {
      surface.heightMap.map{case (i, j, h) =>
        val f = (h < 0.m) select (waterPressure _, airPressure _)
        f(h)
      }
    }

    override def update = {
      if (Controls.Backspace.justPressed) {
        println("reloading")
        val newBiome = new BiomeMap(moisture, surface, latitude)
        val result = buildEcosphere(worldParams, latitude, noise, surface, wind, moisture, newBiome, timeStrings)
        println("loaded")
        result
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

    override def getBiome(i:Int, j:Int):Biome = {
      biome.biomes.get(i, j)
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
      val altitude = Altitude.fromHeight(h)
      (t, h, altitude)
    }

  }

  def create(worldParams:WorldParams):Ecosphere = {
    val seed = worldParams.seed
    val cols = worldParams.size
    val rows = cols
    val latitudeMap = new LatitudeMap(cols, rows)
    def time[T](t: => T) = Timer.timer(() => t)
    val (noise,       perlinTime)   = time(new PerlinNoise(worldParams.perlin).render)
    val (surfaceMap,  surfaceTime)  = time(new SurfaceMap(noise, worldParams.perlin))
    val (windMap,     windTime)     = time(new StaticWindMap(surfaceMap.heightMap, 10000, 1, seed))
    val (moistureMap, moistureTime) = time(new MoistureMap(surfaceMap, latitudeMap.latitude, windMap.arrows, 0.5, cols/2, seed))
    val (biomeMap,    biomeTime)    = time(new BiomeMap(moistureMap, surfaceMap, latitudeMap))
    Biomes.resolver.printConflicts()

    val timeStrings = {
      val sum = (perlinTime + windTime + surfaceTime + biomeTime + moistureTime).toDouble
      def pc(d:Double):Int = ((d/sum)*100).toInt
      val w = pc(windTime)
      val h = pc(surfaceTime)
      val b = pc(biomeTime)
      val m = pc(moistureTime)
      val p = pc(perlinTime)
      Vector(
        "wind     (%02d)" format w,
        "height   (%02d)" format h,
        "biome    (%02d)" format b,
        "moisture (%02d)" format m,
        "perlin   (%02d)" format p)
    }
    buildEcosphere(worldParams, latitudeMap, noise, surfaceMap, windMap, moistureMap, biomeMap, timeStrings)


  }
}

trait Ecosphere {
  val cols:Int
  val rows:Int
  def getLatitude(i:Int, j:Int):LatitudinalRegion = Latitude.Polar
  def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = (Surface.Land, 0.m, Altitude.Abyss)
  def getWind(i:Int, j:Int):Arrow = Arrow(1.dup, 1)
  def getBiome(i:Int, j:Int):Biome = Biomes.Void
  def getPressure(i:Int, j:Int):Pressure = 1.atm
  def getMoisture(i:Int, j:Int):Rainfall = 0.`mm/yr`
  def getTemperature(i:Int, j:Int):Celcius = 20.C
  def getTimeStrings:Vector[String]
  def update:Ecosphere
}
