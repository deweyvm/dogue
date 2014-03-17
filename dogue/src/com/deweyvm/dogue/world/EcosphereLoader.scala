package com.deweyvm.dogue.world

import com.deweyvm.gleany.data.Timer
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.input.Controls

object EcosphereLoader {

  private def makeEcosphere(worldParams:WorldParams) = {
    Timer.printMillisString("Loading map: ", () => {
      Ecosphere.create(worldParams)
    })
  }

  def create(worldParams:WorldParams):Ecosphere = makeEcosphere(worldParams)/*new Ecosphere {
    var string = "Loading..."
    private val innerEco = DogueFuture.createAndRun(() => makeEcosphere(worldParams))
    override val rows: Int = worldParams.size
    override val cols: Int = worldParams.size

    override def getTimeStrings = Vector(string)

    override def update: Ecosphere = {
      if (innerEco.hasFailed) {
        string = "FAILED TO LOAD"
      }
      innerEco.getResult.getOrElse(this)
    }

  }*/
}
