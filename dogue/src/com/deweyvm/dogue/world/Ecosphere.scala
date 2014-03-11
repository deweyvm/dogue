package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.data.{Array2dView, Array2d, Code}
import com.deweyvm.gleany.data._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.world.AtmosphereConstants._
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.Implicits.Pressure
import scala.Some
import com.deweyvm.dogue.common.Implicits.Meters
import java.util.Random

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
      val (w, time) = Timer.timer(() => {
        (windMap.get(i, j) match {
          case Some((_, arr, _)) => arr.some
          case _ => None
        }).getOrElse(super.getWind(i, j))
      })
      wTime += time
      w
    }

    override def getElevation(i:Int, j:Int):(SurfaceType, Meters, AltitudinalRegion) = {
      val (h, time) = Timer.timer(() => {
        getElevationParts(i, j)
      })
      eTime += time
      h
    }

    override def getLatitude(i:Int, j:Int):LatitudinalRegion = {
      latRegions.view.get(i, j)
    }

    override def getBiome(i:Int, j:Int):Biome = {
      val (r, time) = Timer.timer(() => {
        regionMap.get(i, j)
      })
      rTime += time
      r
    }

    override def getPressure(i:Int, j:Int):Pressure = {
      //atmosphereic pressure based on height only for now
      atmosphereMap.get(i, j)
    }


    private var eTime = 0.0
    private var rTime = 0.0
    private var wTime = 0.0
    override def getTimeString:String = {
      val totalTime = rTime + eTime + wTime
      val r = (rTime/totalTime * 100).toInt
      val h = (eTime/totalTime * 100).toInt
      val w = (wTime/totalTime * 100).toInt
      "wind(%d) height(%d) region(%d)" format (w, h, r)
    }


    private val solidElevation = 0 m
    private val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, seed).render

    private val heightMap = new SurfaceMap(noise, worldParams)

    private val windMap:Array2d[(Point2d, Arrow, Color)] = {
      val myHeight = heightMap.landMap.view.map{case (i, j, (t,m)) =>
        val d = m.d
        if (t.isWater) {
          d/10
        } else {
          d/10
        }
      }
      VectorField.perlinWind(solidElevation.d, myHeight, cols, rows, 1, seed).lazyVectors

      //VectorField.const(cols, rows).lazyVectors
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
    val moistureMap = new MoistureMap(cols, rows, heightMap.landMap.view, latitudeMap.view, windMap.view.map{case (i, j,(_,a,_)) => a}, 0.5, 250, random)
    override def getMoisture(i:Int, j:Int):Rainfall = {
      moistureMap.get(i, j)
    }

    private val regionMap:Array2dView[Biome] = {
      //val hexSize = cols/50
      //val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, seed)
      Array2d.tabulate(cols, rows) { case (i, j) =>
        val moisture = moistureMap.get(i, j)
        val temp = 0.5
        val (t, height, alt) = getElevationParts(i, j)
        val lat = latRegions.view.get(i, j)
        Biomes.getBiome(moisture, temp, lat, alt, t)
      }.view
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
  /**
   * force the area at (i, j) to be calculated so there is not a loading delay
   */
  final def view(i:Int, j:Int) {
    getWind(i, j).ignore()
    getElevation(i, j).ignore()
    getLatitude(i, j).ignore()
    getBiome(i, j).ignore()
    getPressure(i, j).ignore()
    getMoisture(i, j).ignore()
    getTemperature(i, j).ignore()
  }
  def getTimeString:String
  def update:Ecosphere
}
