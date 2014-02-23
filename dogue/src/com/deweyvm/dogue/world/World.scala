package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d}
import com.deweyvm.dogue.entities.Tile


object World {
  def create(params:WorldParams):World = {
    val eco = EcosphereLoader.create(params)
    val cycle = CelestialBodies(params.date.startTime, params.size/2, params.date)
    World(params.date.startTime, params, eco, cycle)
  }
}

case class World(t:Long, worldParams:WorldParams, eco:Ecosphere, celestial:CelestialBodies) {
  val cols = eco.cols
  val rows = eco.rows
  def worldTiles:Indexed2d[WorldTile] = Lazy2d.tabulate(cols, rows){ case (i, j) =>
    val (regionIndex, regionColor) = eco.getRegion(i, j)
    val arrow = eco.getWind(i, j)
    val windDir = arrow.direction * arrow.magnitude
    val pressure = eco.getPressure(i, j)
    val (elevation, color, code) = eco.getElevation(i, j)
    val lat = eco.getLatitude(i, j)
    val light = celestial.getSunlight(i, j)
    val season = celestial.getSeason
    val sunTemp = celestial.getSunHeat(i, j)
    val tile = new Tile(code, color, Color.White)

    new WorldTile(elevation, pressure, regionIndex, regionColor, lat, windDir, light, sunTemp, season, tile)
  }

  def update:World = {
    val newT = t + 1
    val newCycle = celestial.copy(t = newT)
    this.copy(celestial = newCycle, eco = eco.update, t = newT)
  }
}




