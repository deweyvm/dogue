package com.deweyvm.dogue.world

import com.deweyvm.gleany.data.Timer
import com.deweyvm.dogue.common.threading.DogueFuture

object EcosphereLoader {

  private def makeEcosphere(worldParams:WorldParams) = {
    Timer.printMillisString("Loading map: ", () => {
      Ecosphere.create(worldParams)
    })

  }
  def create(worldParams:WorldParams) = new Ecosphere {
    private val innerEco = DogueFuture.createAndRun(() => makeEcosphere(worldParams))
    override val rows: Int = worldParams.size
    override val cols: Int = worldParams.size

    override def getTimeStrings: Vector[String] = Vector("Loading...")

    override def update: Ecosphere = innerEco.getResult.getOrElse(this)

  }
}
