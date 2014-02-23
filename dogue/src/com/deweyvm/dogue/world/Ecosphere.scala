package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.common.data.{Code, Indexed2d, Lazy2d, Meters}
import com.deweyvm.gleany.data.{Rectd, Timer, Point2d}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.common.procgen.Arrow
import scala.Some

object Ecosphere {
  def create(worldParams:WorldParams):Ecosphere = {
    AtmosphereConstants.airPressure(3000).println()
    new Ecosphere {
      override val cols = worldParams.size
      override val rows = worldParams.size

      override def update = {
        this
      }

      override def getWind(i:Int, j:Int):Arrow = {
        val (w, time) = Timer.timer(() => {
          windMap.get(i, j).getOrElse((Point2d(0,0), Arrow(Point2d.UnitX, 1), Color.Black)) match {
            case (_, arr, _) => arr
          }
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
        latRegions.get(i, j).getOrElse(Polar)
      }

      override def getRegion(i:Int, j:Int):Color = {
        val (r, time) = Timer.timer(() => {
          regionMap.get(i, j).getOrElse(Color.Black)
        })
        rTime += time
        r
      }

      override def view(i:Int, j:Int) {
        getWind(i, j).ignore()
        getElevation(i, j).ignore()
        getLatitude(i, j).ignore()
        getRegion(i, j).ignore()
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
      private val noise = new PerlinNoise(1/worldParams.period.toDouble, worldParams.octaves, worldParams.size, worldParams.seed).lazyRender

      private val windMap: Lazy2d[(Point2d, Arrow, Color)] = {
        VectorField.perlinWind(solidElevation.d, noise, cols, rows, 1, worldParams.seed).lazyVectors
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
            val bowl = 0//(d1-inner)/(1 - inner - mring)
            (bowl + h).clamp(-1, 1)
          }
          d * 10000 m
        })
      }

      private val latRegions:Indexed2d[LatitudinalRegion] = {
        val max = cols/2
        Lazy2d.tabulate(cols, rows){ case (i, j) =>
          val x = cols/2 - i
          val y = rows/2 - j
          val lat = (x*x + y*y).sqrt/max
          LatitudinalRegion.getRegion(lat)
        }
      }

      private val regionMap:Indexed2d[Color] = {
        val hexSize = cols/150
        val hexGrid = new HexGrid(hexSize, cols/hexSize, 2*rows/hexSize, hexSize/4, worldParams.seed)
        val colors = (0 until hexGrid.graph.nodes.length).map {_ => Color.randomHue()}
        val graph = hexGrid.graph
        val colorMap = (colors zip graph.nodes).map { case (color, poly) =>
          (poly.self, color)
        }.toMap
        Lazy2d.tabulate(cols, rows){ case (i, j) =>
          heightMap.get(i, j) match {
            case Some(d) if d > 0  =>
              hexGrid.pointInPoly(i, j) match {
                case Some(poly) => colorMap(poly)
                case None => Color.Black
              }
            case _ => Color.Black
          }
        }
      }

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
          } else if (h < 8000.m){
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
}

trait Ecosphere {
  val cols:Int
  val rows:Int
  def getLatitude(i:Int, j:Int):LatitudinalRegion
  def getElevation(i:Int, j:Int):(Meters, Color, Code)
  def getWind(i:Int, j:Int):Arrow
  def getRegion(i:Int, j:Int):Color

  /**
   * force the area at (i, j) to be calculated so there is not a loading delay
   */
  def view(i:Int, j:Int)
  def getTimeString:String
  def update:Ecosphere
}
