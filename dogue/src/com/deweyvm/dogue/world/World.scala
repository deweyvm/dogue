package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d}
import com.deweyvm.dogue.entities.Tile


object World {
  def create(params:WorldParams):World = {
    val eco = EcosphereLoader.create(params)
    val cycle = Nychthemera(0, params.size/2)
    World(params, eco, cycle)
  }
}

case class World(worldParams:WorldParams, eco:Ecosphere, cycle:Nychthemera) {
  val cols = eco.cols
  val rows = eco.rows
  def worldTiles:Indexed2d[WorldTile] = Lazy2d.tabulate(cols, rows){ case (i, j) =>
    val region = eco.getRegion(i, j)
    val arrow = eco.getWind(i, j)
    val windDir = arrow.direction * arrow.magnitude
    val pressure = eco.getPressure(i, j)
    val (elevation, color, code) = eco.getElevation(i, j)
    val lat = eco.getLatitude(i, j)
    val light = cycle.getSunlight(i, j)
    val season = cycle.getSeason
    val sunTemp = cycle.getSunHeat(i, j)
    val tile = new Tile(code, color, Color.White)

    new WorldTile(elevation, pressure, region, lat, windDir, light, sunTemp, season, tile)
  }

  def update:World = {
    this.copy(cycle = cycle.update, eco = eco.update)
  }
}




