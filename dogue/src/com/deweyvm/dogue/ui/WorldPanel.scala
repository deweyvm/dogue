package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.data.{Array2dView, Pointer, Code}
import com.deweyvm.dogue.common.procgen.{PerlinParams, VectorField}
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.world.WorldParams
import com.deweyvm.dogue.world.ArrayViewer
import com.deweyvm.dogue.world.DateConstants
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.world.biomes.Biomes
import sun.java2d.Surface

trait MapState {
  def draw(t:WorldTile, i:Int, j:Int):Unit
}

object MapState {
  var maxWind = 1.0
  case object Wind extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
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
      new Tile(code, /*t.tile.bgColor*/VectorField.magToColor(magnitude, maxWind), Color.White).draw(i, j)
    }
  }

  case object Topography extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
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
      t.tile.copy(bgColor = color, fgColor = Color.White, code = t.biome.code).draw(i, j)

    }
  }

  case object Latitude extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
      t.tile.copy(bgColor = t.latitude.color).draw(i, j)
    }
  }

  case object Biome extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
      val color = t.biomeColor//biomes.colorMap(t.biome)
      t.tile.copy(bgColor = color, code = t.biome.code).draw(i, j)
    }
  }

  case object Nychthemera extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
      val light = Color.fromHsb(t.daylight.toFloat/2)
      t.tile.copy(bgColor = light).draw(i, j)
    }
  }

  case object Moisture extends MapState {
    def draw(t:WorldTile, i:Int, j:Int) {
      if (t.biome.spec.surface.isWater) {
        t.tile.copy(bgColor = Color.Black).draw(i, j)
      } else {
        val c = Color.fromHsb(t.moisture.d.toFloat/(10000*2) % 1)
        t.tile.copy(bgColor = c).draw(i, j)
      }
    }
  }
  val All = Vector(Wind, Topography, Biome, Moisture, Latitude, Nychthemera)
  def getPointer:Pointer[MapState] = Pointer.create(All, 0)
}

trait ZoomState
object ZoomState {
  case object Full extends ZoomState
  case object Mini extends ZoomState
  val All = Vector(Mini, Full)
  def getPointer:Pointer[ZoomState] = Pointer.create(All, 0)
}
object WorldPanel {
  var t  = 0L


  def create(rect:Recti,
             tooltipWidth:Int,
             tooltipHeight:Int,
             minimapSize:Int,
             bgColor:Color,
             size:Int):WorldPanel = {

    val seed = System.nanoTime
    println("Seed: " + seed + "L")
    val date = DateConstants(framesPerDay = 60*60*24*60)
    val perlin = PerlinParams(size/4, 8, size, seed)
    val params = WorldParams(minimapSize, perlin, date)
    val world = World.create(params)
    val tooltip = InfoPanel.makeNew(Recti(1, 1, tooltipWidth, tooltipHeight), bgColor)
    val worldViewer = ArrayViewer(rect.width, rect.height, size/2, size/2, Controls.AxisX, Controls.AxisY)
    new WorldPanel(rect, bgColor, world, worldViewer, tooltip, params.minimapSize, ZoomState.getPointer, MapState.getPointer)
  }
}

case class WorldPanel(override val rect:Recti,
                      bgColor:Color,
                      world:World,
                      view:ArrayViewer,
                      tooltip:InfoPanel,
                      minimapSize:Int,
                      zoomState:Pointer[ZoomState],
                      mapState:Pointer[MapState])
  extends Panel(rect, bgColor) {
  val (iSpawn, jSpawn) = (0,0)
  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }
  val regionSize = 16
  val miniDiv = world.cols/minimapSize//minimap.div //(4096*16)/69
  override def update:WorldPanel = {
    val newTooltip = getTooltip
    val newWorld = world.update
    val incr = if (Controls.RShift.justPressed) {
      1
    } else if (Controls.Backspace.justPressed) {
      -1
    } else {
      0
    }
    val newZoomState = zoomState.updated(incr)

    val newMapState = if (Controls.Space.justPressed) {
      mapState.updated(1)
    } else if (Controls.LShift.justPressed) {
      mapState.updated(-1)
    } else {
      mapState
    }

    val newView = view.update(getTiles, getScale)

    this.copy(world = newWorld,
              view = newView,
              tooltip = newTooltip.update,
              zoomState = newZoomState,
              mapState = newMapState)

  }

  def getScale:Int = zoomState.get match {
    case ZoomState.Full => 1
    case ZoomState.Mini => miniDiv
  }

  def getTiles:Array2dView[WorldTile] = zoomState.get match {
    case ZoomState.Full => world.worldTiles
    case ZoomState.Mini => world.worldTiles
  }


  private def getTooltip:InfoPanel =  {
    def getTooltip(t:WorldTile):Tooltip = zoomState.get match {
      case ZoomState.Full => t.regionTooltip
      case ZoomState.Mini => t.fullTooltip
    }
    val fresh = InfoPanel.makeNew(Recti(1, 1, tooltip.width, tooltip.height), bgColor)
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTooltip(getTiles.get(i, j)).some
    tip.foldLeft(fresh){ case (acc, tt) =>
      acc.addLines(tt.lines, bgColor, tt.color)
    }
  }



  private def drawTiles() {
    val draw = mapState.get.draw _

    val tiles = world.worldTiles
    zoomState.get match {
      case ZoomState.Full =>
        view.draw(tiles, x, y, draw)
      case ZoomState.Mini =>
        view.scaled(miniDiv).draw(tiles.sample(miniDiv), x, y, draw)
    }
  }


  override def draw() {
    super.draw()
    drawTiles()
    tooltip.draw()
    drawName()
    drawDebug()
    drawDate()

  }

  private def drawDebug() {
    val strs = world.eco.getTimeStrings
    strs.zipWithIndex foreach { case (s, i) =>
      Text.fromString(s, Color.Black, Color.White).draw(0, height + 1 - i)
    }

  }

  private def drawName() {
    val name = world.worldParams.name
    val xName = x + (width - name.length)/2
    Text.fromString(name, Color.Black, Color.White).draw(xName,0)
  }

  private def drawDate() {
    val time = world.cycle.date.getString
    val xName = x + (width - time.length)/2
    Text.fromString(time, Color.Black, Color.White).draw(xName, height)
  }
}
