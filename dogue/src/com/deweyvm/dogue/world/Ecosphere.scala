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

object Ecosphere {
  def create(worldParams:WorldParams):Ecosphere = {
    new Ecosphere {
      outer =>
      val seed = worldParams.seed
      override val cols = worldParams.size
      override val rows = worldParams.size
      var perlinTime = 0L
      var heightTime = 0L
      var windTime = 0L
      var biomeTime = 0L
      var moistureTime = 0L

      override def getTimeString:String = {
        val sum = (perlinTime + windTime + heightTime + biomeTime + moistureTime).toDouble
        def pc(d:Double):Int = ((d/sum)*100).toInt
        val w = pc(windTime)
        val h = pc(heightTime)
        val b = pc(biomeTime)
        val m = pc(moistureTime)
        val p = pc(perlinTime)
        "wind(%d) height(%d) biome(%d) moisture(%d) perlin(%d)" format (w, h, b, m, p)
      }

      private val noise = {
        val (p, t) = Timer.timer(() => new PerlinNoise(worldParams.perlin).render)
        perlinTime = t
        p
      }

      private val surfaceMap = {
        val (s, t) = Timer.timer(() => new SurfaceMap(noise, worldParams.perlin))
        heightTime = t
        s
      }

      private val windMap = {
        val (w, t) = Timer.timer(() => new StaticWindMap(surfaceMap.heightMap, 10000, 1, seed))
        windTime = t
        w
      }

      private val atmosphereMap:Array2dView[Pressure] = {
        surfaceMap.heightMap.map{case (i, j, h) =>
          val f = (h < 0.m) select (waterPressure _, airPressure _)
          f(h)
        }
      }

      private val latitudeMap = new LatitudeMap(cols, rows)


      val random = new Random(worldParams.seed)
      val moistureMap = {
        val (m, t) = Timer.timer(() => {
          new MoistureMap(cols, rows, surfaceMap, latitudeMap.latitude, windMap.arrows, 0.5, cols/2, random)
        })
        moistureTime = t
        m
      }

      private val regionMap:Array2dView[Biome] = {
        //val hexSize = cols/50
        //val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, seed)
        val (r, t) = Timer.timer(() => {

        })
        Biomes.resolver.printConflicts()
        biomeTime = t
        r

      }

      override def update = {
        this
      }

      override def getWind(i:Int, j:Int):Arrow = windMap.arrows.get(i, j)

      override def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
        getElevationParts(i, j)
      }

      override def getLatitude(i:Int, j:Int):LatitudinalRegion = {
        latitudeMap.regions.get(i, j)
      }

      override def getBiome(i:Int, j:Int):Biome = {
        regionMap.get(i, j)
      }

      override def getPressure(i:Int, j:Int):Pressure = {
        atmosphereMap.get(i, j)
      }


      override def getMoisture(i:Int, j:Int):Rainfall = {
        moistureMap.get(i, j)
      }

      private def getElevationParts(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
        val t = surfaceMap.landMap.get(i, j)
        val h = surfaceMap.heightMap.get(i, j)
        val altitude = Altitude.fromHeight(h)
        (t, h, altitude)
      }

    }
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
  def getTimeString:String
  def update:Ecosphere
}
