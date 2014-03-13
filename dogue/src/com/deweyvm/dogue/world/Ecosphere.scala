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
  def create(worldParams:WorldParams):Ecosphere = new Ecosphere {
    outer =>
    val seed = worldParams.seed
    override val cols = worldParams.size
    override val rows = worldParams.size

    override def update = {
      this
    }

    override def getWind(i:Int, j:Int):Arrow = {
      (windMap.get(i, j) match {
        case Some((_, arr, _)) => arr.some
        case _ => None
      }).getOrElse(super.getWind(i, j))
    }

    override def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
      getElevationParts(i, j)
    }

    override def getLatitude(i:Int, j:Int):LatitudinalRegion = {
      latRegions.view.get(i, j)
    }

    override def getBiome(i:Int, j:Int):Biome = {
      regionMap.get(i, j)
    }

    override def getPressure(i:Int, j:Int):Pressure = {
      //atmosphereic pressure based on height only for now
      atmosphereMap.get(i, j)
    }

    var heightTime = 0L
    var windTime = 0L
    var biomeTime = 0L
    var moistureTime = 0L

    override def getTimeString:String = {
      val sum = (windTime + heightTime + biomeTime + moistureTime).toDouble
      def pc(d:Double):Int = ((d/sum)*100).toInt
      val w = pc(windTime)
      val h = pc(heightTime)
      val b = pc(biomeTime)
      val m = pc(moistureTime)
      "wind(%d) height(%d) biome(%d) moisture(%d)" format (w, h, b, m)
    }

    private val noise = new PerlinNoise(worldParams.perlin).render

    private val heightMap = {
      val (s, t) = Timer.timer(() => new SurfaceMap(noise, worldParams.perlin))
      heightTime = t
      s
    }

    private val windMap:Array2d[(Point2d, Arrow, Color)] = {
      val (w, t) = Timer.timer(() => {
        val myHeight = heightMap.landMap.view.map { case (i, j, (_,m)) =>
          m.d
        }
        //VectorField.const(cols, rows).lazyVectors
        //VectorField.perlinWind(0.m.d, myHeight, cols, rows, 1, seed).lazyVectors
        VectorField.perlinWind(0.m.d, myHeight, 10000, 80, cols, rows, 1, seed).lazyVectors
      })

      windTime = t
      w
    }

    private val atmosphereMap:Array2dView[Pressure] = {
      heightMap.landMap.view.map{case (i, j, (_,h)) =>
        val f = (h < 0) select (waterPressure _, airPressure _)
        f(h)
      }
    }

    private val latitudeMap:Array2d[Double] = {
      val max = cols/2
      Array2d.tabulate(cols, rows){ case (i, j) =>
        val x = (cols/2 - i).toDouble
        val y = (rows/2 - j).toDouble
        (x*x + y*y).sqrt/max
      }
    }

    private val latRegions:Array2d[LatitudinalRegion] = latitudeMap.map{ case (i, j, l) =>
      Latitude.getLatitude(l)
    }
    val random = new Random(worldParams.seed)
    val moistureMap = {
      val (m, t) = Timer.timer(() => {
        new MoistureMap(cols, rows, heightMap.landMap.view, latitudeMap.view, windMap.view.map{case (i, j,(_,a,_)) => a}, 0.5, cols/2, random)
      })
      moistureTime = t
      m
    }
    override def getMoisture(i:Int, j:Int):Rainfall = {
      moistureMap.get(i, j)
    }

    private val regionMap:Array2dView[Biome] = {
      //val hexSize = cols/50
      //val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, seed)
      val (r, t) = Timer.timer(() => {
        Array2d.tabulate(cols, rows) { case (i, j) =>
          val moisture = moistureMap.get(i, j)
          val (t, _, alt) = getElevationParts(i, j)
          val lat = latRegions.view.get(i, j)
          Biomes.getBiome(moisture, lat, alt, t)
        }.view
      })
      Biomes.resolver.printConflicts()
      biomeTime = t
      r

    }

    private def getElevationParts(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
      val (t, h) = heightMap.get(i, j)
      val altitude = Altitude.fromHeight(h)
      (t, h, altitude)
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
