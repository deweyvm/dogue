package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.common.data.{Pointer, Code, Indexed2d}
import com.deweyvm.dogue.common.procgen.VectorField
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.world.WorldParams
import com.deweyvm.dogue.world.ArrayViewer
import com.deweyvm.dogue.world.DateConstants
import com.deweyvm.dogue.ui.ZoomState.{Mini, Full}

trait MapState

object MapState {
  case object Wind extends MapState
  case object Elevation extends MapState
  case object Latitude extends MapState
  case object Biome extends MapState
  case object Nychthemera extends MapState
  val All = Vector(Wind, Elevation, Latitude, Biome, Nychthemera)
  def getPointer:Pointer[MapState] = Pointer.create(All, 0)
}

trait ZoomState
object ZoomState {
  case object Region extends ZoomState
  case object Full extends ZoomState
  case object Mini extends ZoomState
  val All = Vector(Region, Full, Mini)
  def getPointer:Pointer[ZoomState] = Pointer.create(All, 0)
}
object WorldPanel {



  def create(rect:Recti,
             tooltipWidth:Int, tooltipHeight:Int,
             bgColor:Color, size:Int):WorldPanel = {
    val date = DateConstants(framesPerDay = 60*60*24*60)
    val params = WorldParams(69, size/4, 22, size, date, 5)
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
  import WorldPanel._
  val (iSpawn, jSpawn) = (0,0)
  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }
  val regionSize = 16
  val miniDiv = world.cols/minimapSize//minimap.div //(4096*16)/69
  val regionDiv = 16
  override def update:WorldPanel = {
    val newTooltip = getTooltip
    val newWorld = world.update
    val incr = if (Controls.Enter.justPressed) {
      1
    } else if (Controls.Backspace.justPressed) {
      -1
    } else {
      0
    }
    val newZoomState = zoomState.updated(incr)

    val newMapState = if (Controls.Space.justPressed) {
      mapState.updated(1)
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
    case ZoomState.Region => 1
    case ZoomState.Full => regionDiv
    case ZoomState.Mini => miniDiv
  }

  def getTiles:Indexed2d[WorldTile] = zoomState.get match {
    case ZoomState.Region => world.worldTiles
    case ZoomState.Full => world.worldTiles
    case ZoomState.Mini => world.worldTiles
  }


  private def getTooltip:InfoPanel =  {
    def getTooltip(t:WorldTile):Tooltip = zoomState.get match {
      case ZoomState.Region => t.regionTooltip
      case ZoomState.Full => t.fullTooltip
      case ZoomState.Mini => t.fullTooltip
    }
    val fresh = InfoPanel.makeNew(Recti(1, 1, tooltip.width, tooltip.height), bgColor)
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTiles.get(i, j) map getTooltip
    tip.foldLeft(fresh){ case (acc, tt) =>
      acc.addLines(tt.lines, bgColor, tt.color)
    }
  }

  override def draw() {
    super.draw()
    def drawWorldTile(i:Int, j:Int, t:WorldTile) = {
      import MapState._
      mapState.get match {
        case Nychthemera =>
          val light = Color.fromHsb(t.daylight.toFloat/2)
          t.tile.copy(bgColor = light).draw(i, j)
        case Latitude =>
          t.tile.copy(bgColor = t.latitude.color).draw(i, j)
        case Elevation =>
          t.tile.draw(i, j)
        case Biome =>
          t.tile.copy(bgColor = t.regionColor, code = Code.` `).draw(i, j)
        case Wind =>
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
          new Tile(code, /*t.tile.bgColor*/VectorField.magToColor(t.wind.magnitude), Color.White).draw(i, j)
      }

    }
    val tiles = world.worldTiles
    zoomState.get match {
      case ZoomState.Region =>
        view.draw(world.worldTiles, x, y, drawWorldTile, WorldTile.Blank)
      case ZoomState.Full =>
        view.scaled(regionDiv).draw(world.worldTiles.sample(regionDiv), x, y, drawWorldTile, WorldTile.Blank)
      case ZoomState.Mini =>
        view.scaled(miniDiv).draw(world.worldTiles.sample(miniDiv), x, y, drawWorldTile, WorldTile.Blank)
    }


    tooltip.draw()
    drawName()
    drawDebug()
    drawTime()
    //
  }

  private def drawDebug() {
    val s = world.eco.getTimeString
    new Text(s, Color.Black, Color.White).draw(0, height + 1)
  }

  private def drawName() {
    val name = world.worldParams.name
    val xName = x + (width - name.length)/2
    new Text(name, Color.Black, Color.White).draw(xName,0)
  }

  private def drawTime() {
    val time = world.celestial.date.getString
    val xName = x + (width - time.length)/2
    new Text(time, Color.Black, Color.White).draw(xName, height)
  }
}
