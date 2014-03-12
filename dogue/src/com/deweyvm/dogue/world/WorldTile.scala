package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui.Tooltip
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.world.biomes.{Biomes, Biome}

object WorldTile {
  val Blank = WorldTile(0 m, Altitude.Abyss, Surface.Water, 1 atm, 0, Biomes.Void, Latitude.Polar, Point2d.UnitX, 0, 0,  Summer, Tile.Blank)
}

case class WorldTile(height:Meters,
                     altitude:AltitudinalRegion,
                     surface:SurfaceType,
                     pressure:Pressure,
                     moisture:Rainfall,
                     biome:Biome,
                     latitude:LatitudinalRegion,
                     wind:Point2d,
                     daylight:Double,
                     sunTemp:Celcius,
                     season:Season,
                     tile:Tile) {
  def fullTooltip:Tooltip = Tooltip(Color.Red, Vector(
    "Height   : %f" format height.d,
    "AltRegion: %s" format altitude,
    "Region   : %s" format biome,
    "Surface  : %s" format surface,
    "Latitude : %s" format latitude,
    "Wind     : %.2f" format wind.magnitude,
    "Daylight : %.2f" format daylight,
    "Mean Temp: %.2f" format sunTemp.d,
    "Pressure : %.2f" format pressure.d,
    "Rainfall : %.2f" format moisture.d,
    "",
    "Season: %s" format season.toString
  ))

  def regionTooltip:Tooltip = fullTooltip.copy(color = Color.White)
}
