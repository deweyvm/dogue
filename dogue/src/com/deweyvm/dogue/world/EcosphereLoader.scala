package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Code, Meters}
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.common.Implicits
import Implicits._

object EcosphereLoader {

  private def makeEcosphere(worldParams:WorldParams) = {
    val cols = worldParams.minimapSize
    val rows = worldParams.minimapSize
    val div = worldParams.size/worldParams.minimapSize
    val minimapTiles = for (i <- 0 until cols; j <- 0 until rows) yield {
      (i*div, j*div)
    }
    val eco = Ecosphere.create(worldParams)
    minimapTiles foreach { case (i, j) =>
      eco.view(i, j)
    }
    eco
  }
  def create(worldParams:WorldParams) = new Ecosphere {
    private val innerEco = DogueFuture.createAndRun(() => makeEcosphere(worldParams))
    override val rows: Int = worldParams.size
    override val cols: Int = worldParams.size

    override def getWind(i: Int, j: Int): Arrow = new Arrow(Point2d.UnitX, 0)

    override def getRegion(i: Int, j: Int): Color = Color.Black

    override def getElevation(i: Int, j: Int): (Meters, Color, Code) = (0.m, Color.Black, Code.` `)

    override def getLatitude(i: Int, j: Int): LatitudinalRegion = Polar

    override def view(i:Int, j:Int) = {}

    override def getTimeString: String = "Loading..."

    override def update: Ecosphere = innerEco.getResult.getOrElse(this)

  }
}
