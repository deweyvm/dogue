package com.deweyvm.dogue.world

import com.deweyvm.gleany.data.Timer
import com.deweyvm.dogue.common.threading.DogueFuture

object EcosphereLoader {

  private def makeEcosphere(worldParams:WorldParams) = {
    Timer.printMillisString("Loading map: ", () => {
      val cols = worldParams.minimapSize
      val rows = worldParams.minimapSize
      val div = worldParams.size/worldParams.minimapSize
      val minimapTiles = for (i <- 0 until cols; j <- 0 until rows) yield {
        (i*div, j*div)
      }
      val eco = Ecosphere.create(worldParams)
      minimapTiles.foreach { case (i, j) =>
        eco.view(i, j)
      }
      eco
    })

  }
  def create(worldParams:WorldParams) = new Ecosphere {
    private val innerEco = DogueFuture.createAndRun(() => makeEcosphere(worldParams))
    override val rows: Int = worldParams.size
    override val cols: Int = worldParams.size

    override def getTimeString: String = "Loading..."

    override def update: Ecosphere = innerEco.getResult.getOrElse(this)

  }
}
