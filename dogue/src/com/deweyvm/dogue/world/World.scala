package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Code, Array2dView, Array2d}
import com.deweyvm.dogue.entities.Tile


object World {
  def create(params:WorldParams):World = {
    val eco = EcosphereLoader.create(params)
    val cycle = CelestialBodies(params.date.startTime, params.size/2, params.date)
    World(params.date.startTime, params, eco, cycle)
  }
}

case class World(t:Long, worldParams:WorldParams, eco:Ecosphere, cycle:CelestialBodies) {
  outer =>
  val cols = eco.cols
  val rows = eco.rows

  def worldTiles:Array2dView[WorldTile] = new Array2dView[WorldTile] {
    val cols = outer.cols
    val rows = outer.rows
    def get(i:Int, j:Int) = {
      val arrow = eco.getWind(i, j)
      val windDir = arrow.direction * arrow.magnitude
      val pressure = eco.getPressure(i, j)
      val (surface, elevation, altitude) = eco.getElevation(i, j)
      val lat = eco.getLatitude(i, j)
      val light = cycle.getSunlight(i, j)
      val moisture = eco.getMoisture(i, j)
      val season = cycle.getSeason
      val sunTemp = cycle.getSunHeat(i, j)
      val tile = new Tile(Code.` `, Color.Black, Color.White)
      val biome = eco.getBiome(i, j)
      new WorldTile(elevation, altitude, surface, pressure, moisture, biome, lat, windDir, light, sunTemp, season, tile)
    }

  }

  def update:World = {
    val newT = t + 1
    val newCycle = cycle.copy(t = newT)
    val newEco = eco.update
    this.copy(cycle = newCycle, eco = newEco, t = newT)
  }
}




