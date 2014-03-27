package com.deweyvm.dogue.ui.world

import com.deweyvm.dogue.world.WorldTile
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.{Pointer, Code}
import com.deweyvm.dogue.common.procgen.VectorField
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

trait MapState {
  def renderTile(t:WorldTile):Tile
}

object MapState {
  var maxWind = 1.0
  case object Wind extends MapState {
    def renderTile(t:WorldTile) = {
      val dir = t.wind.normalize
      val max = math.max(math.abs(dir.x), math.abs(dir.y))
      val code =
        if (math.abs(math.abs(dir.x) - math.abs(dir.y)) > max/2) {
          if (math.abs(dir.x) > math.abs(dir.y)) {
            if (dir.x > 0) {
              Code.→
            } else {
              Code.`←`
            }
          } else {
            if (dir.y > 0) {
              Code.↓
            } else {
              Code.↑
            }
          }
        } else {
          (math.signum(dir.x).toInt, math.signum(dir.y).toInt) match {
            case (1, -1) => Code./
            case (-1, 1) => Code./
            case (1, 1) => Code.\
            case (-1, -1) => Code.\
            case _ => Code.`?`
          }
        }
      val magnitude = t.wind.magnitude
      if (magnitude > maxWind) {
        maxWind = magnitude
      }
      Tile(code, /*t.tile.bgColor*/VectorField.magToColor(magnitude, maxWind), Color.White)
    }
  }

  case object Topography extends MapState {
    def renderTile(t:WorldTile) = {
      val (color1,color2) = t.surface.isWater match {
        case false =>
          (Color.DarkGreen, Color.Grey)
        case true =>
          (Color.DarkBlue, Color.Cyan)
      }
      val d = (t.height.d/4000).toFloat
      val color = if (d > 0.8) {
        color2.dim((2 - d).clamp(1, 999))
      } else {
        color1.brighten(d)
      }
      t.tile.copy(bgColor = color, fgColor = Color.White, code = t.biome.code)

    }
  }

  case object Latitude extends MapState {
    def renderTile(t:WorldTile) = {
      t.tile.copy(bgColor = t.latitude.color)
    }
  }

  case object Biome extends MapState {
    def renderTile(t:WorldTile) = {
      val color = t.biomeColor
      t.tile.copy(bgColor = color, code = t.biome.code)
    }
  }

  case object Nychthemera extends MapState {
    def renderTile(t:WorldTile) = {
      val light = Color.fromHsb(t.daylight.toFloat/2)
      t.tile.copy(bgColor = light)
    }
  }

  case object Moisture extends MapState {
    def renderTile(t:WorldTile) = {
      if (t.biome.spec.surface.isWater) {
        t.tile.copy(bgColor = Color.Black)
      } else {
        val c = Color.fromHsb(t.moisture.d.toFloat/(10000*2) % 1)
        t.tile.copy(bgColor = c)
      }
    }
  }
  val All = Vector(Wind, Topography, Biome, Moisture, Latitude, Nychthemera)
  def getPointer:Pointer[MapState] = Pointer.create(All, 0)
}
