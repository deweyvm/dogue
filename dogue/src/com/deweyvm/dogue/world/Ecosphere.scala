package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.data.{Code, Indexed2d, Lazy2d}
import com.deweyvm.gleany.data._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.world.AtmosphereConstants._
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.dogue.common.Implicits.Pressure
import scala.Some
import com.deweyvm.dogue.common.Implicits.Meters

object Ecosphere {
  def create(worldParams:WorldParams):Ecosphere = new Ecosphere {
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

    override def getElevation(i:Int, j:Int):(Meters, Color, Code) = {
      val (h, time) = Timer.timer(() => {
        getElevationTriple(i, j)
      })
      eTime += time
      h
    }

    override def getLatitude(i:Int, j:Int):LatitudinalRegion = {
      latRegions.get(i, j).getOrElse(super.getLatitude(i, j))
    }

    override def getRegion(i:Int, j:Int):(Int, Color) = {
      val (r, time) = Timer.timer(() => {
        regionMap.get(i, j).getOrElse(super.getRegion(i, j))
      })
      rTime += time
      r
    }

    override def getPressure(i:Int, j:Int):Pressure = {
      //atmosphereic pressure based on height only for now
      atmosphereMap.get(i, j).getOrElse(super.getPressure(i, j))
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

    private val maxElevation = 10000.0 m
    private val solidElevation = 0 m
    private val solidTier = solidElevation.d/maxElevation.d - 1

    private val border = math.min(cols, rows)/2 - 10
    private val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, seed).lazyRender

    private val windMap: Lazy2d[(Point2d, Arrow, Color)] = {
      //VectorField.perlinWind(solidElevation.d, noise, cols, rows, 1, seed).lazyVectors

      VectorField.simpleSpiral(cols, rows).lazyVectors
    }

    private def perlinToHeight(t:Double) = {
      val tm = t
      val sign = math.signum(t)
      tm*tm*sign
    }

    private val heightMap:Indexed2d[Meters] = {
      noise.map({ case (i, j, p) =>
        val h = perlinToHeight(p)
        val x = (cols/2 - i).toDouble
        val y = (rows/2 - j).toDouble
        val dist = math.sqrt(x*x + y*y)
        val d1 = dist/(cols/2.toDouble)
        val inner = 0.8
        val ring = 0.92
        val mring = 1 - ring
        assert(dist > cols/2 || (d1 >= 0 && d1 <= 1.0), "bad at (%d, %d), %.2f" format (i, j, d1))
        val d = if (d1 > ring) {
          0//1
        } else if (d1 < inner) {
          h
        } else {
          val bowl = (d1-inner)/(1 - inner - mring)
          (bowl + h).clamp(-1, 1)
        }
        d * 10000 m
      })
    }

    private val atmosphereMap:Indexed2d[Pressure] = {
      heightMap.map{case (i, j, h) =>
        val f = (h < 0) select (waterPressure _, airPressure _)
        f(h)
      }
    }

    private val latRegions:Indexed2d[LatitudinalRegion] = {
      val max = cols/2
      Lazy2d.tabulate(cols, rows){ case (i, j) =>
        val x = (cols/2 - i).toDouble
        val y = (rows/2 - j).toDouble
        val lat = (x*x + y*y).sqrt/max
        LatitudinalRegion.getRegion(lat)
      }
    }

    private val regionMap:Indexed2d[(Int, Color)] = {
      val hexSize = cols/150
      val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, seed)
      val colors = (0 until hexGrid.graph.nodes.length).map {_ => Color.randomHue()}
      val graph = hexGrid.graph
      val colorMap = (colors.zipWithIndex zip graph.nodes).map { case ((color, index), poly) =>
        (poly.self, (index, color))
      }.toMap
      Lazy2d.tabulate(cols, rows){ case (i, j) =>
        heightMap.get(i, j) match {
          case Some(d) if d > 0  =>
            hexGrid.pointInPoly(i, j) match {
              case Some(poly) => colorMap(poly)
              case None => (0, Color.Black)
            }
          case _ => (0, Color.Black)
        }
      }
    }

    val moisture = new Moisture(cols, rows, heightMap, windMap.map{case (i, j,(_,a,_)) => a}, 1,200)
    override def getMoisture(i:Int, j:Int):Double = moisture.map.get(i, j).getOrElse(super.getMoisture(i, j))


    private def getElevationTriple(i:Int, j:Int):(Meters, Color, Code) = {
      val h = heightMap.get(i, j).getOrElse(maxElevation)
      val (color, code) =
        if (h <= 0.m) {
          val cr = Color.Blue.dim((math.abs(h.d)/maxElevation.d).toFloat)
          (cr, Code.≈)
        } else if (h < 50.m) {
          (Color.Yellow, Code.`.`)
        } else if (h < 750.m) {
          (Color.Green, Code.`"`)
        } else if (h < 5000.m) {
          (Color.DarkGreen, Code.♠)
        } else if (h < 6000.m) {
          (Color.DarkGrey, Code.▲)
        } else if (h < 7000.m) {
          (Color.Grey, Code.▲)
        } else if (h < 8000.m) {
          (Color.White.dim(1.3f), Code.▲)
        } else if (h < 9000.m) {
          (Color.White.dim(1.2f), Code.▲)
        } else {
          (Color.White.dim(1.1f), Code.▲)
        }
      (h, color, code)
    }

  }
}

trait Ecosphere {
  val cols:Int
  val rows:Int
  def getLatitude(i:Int, j:Int):LatitudinalRegion = Polar
  def getElevation(i:Int, j:Int):(Meters, Color, Code) = (0.m, Color.Black, Code.` `)
  def getWind(i:Int, j:Int):Arrow = Arrow(1.dup, 1)
  def getRegion(i:Int, j:Int):(Int, Color) = (0,Color.Black)
  def getPressure(i:Int, j:Int):Pressure = 1.atm
  def getMoisture(i:Int, j:Int):Double = 0
  /**
   * force the area at (i, j) to be calculated so there is not a loading delay
   */
  final def view(i:Int, j:Int) {
    getWind(i, j).ignore()
    getElevation(i, j).ignore()
    getLatitude(i, j).ignore()
    getRegion(i, j).ignore()
    getPressure(i, j).ignore()
    getMoisture(i, j).ignore()
  }
  def getTimeString:String
  def update:Ecosphere
}
