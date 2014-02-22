package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.common.data.{Code, Indexed2d}
import com.deweyvm.dogue.common.procgen.VectorField
import com.deweyvm.dogue.entities.Tile

object WorldPanel {
  trait MapState {
    def prev:MapState
    def next:MapState
  }

  case object Wind extends MapState {
    override def prev = Biome
    override def next = Elevation
  }
  case object Elevation extends MapState {
    override def prev = Wind
    override def next = Latitude
  }
  case object Latitude extends MapState {
    override def prev = Elevation
    override def next = Biome
  }
  case object Biome extends MapState {
    override def prev = Latitude
    override def next = Nychthemera
  }

  case object Nychthemera extends MapState {
    override def prev = Biome
    override def next = Wind
  }


  trait ZoomState {
    def next:Option[ZoomState]
    def prev:Option[ZoomState]
  }

  case object Region extends ZoomState {
    override def next = None
    override def prev = Full.some
  }
  case object Full extends ZoomState {
    override def next = Region.some
    override def prev = Mini.some
  }
  case object Mini extends ZoomState {
    override def next = Full.some
    override def prev = None
  }

  def create(rect:Recti,
             tooltipWidth:Int, tooltipHeight:Int,
             bgColor:Color, size:Int):WorldPanel = {
    val world = World.create(WorldParams(size/4, 22, size, 5))
    val tooltip = InfoPanel.makeNew(Recti(1, 1, tooltipWidth, tooltipHeight), bgColor)
    val minimap = new Minimap(world, 69)
    val worldViewer = ArrayViewer(rect.width, rect.height, 0, 0, Controls.AxisX, Controls.AxisY)
    new WorldPanel(rect, bgColor, world, worldViewer, tooltip, minimap, Mini, Nychthemera)
  }
}




case class WorldPanel(override val rect:Recti,
                      bgColor:Color,
                      world:World,
                      view:ArrayViewer,
                      tooltip:InfoPanel,
                      minimap:Minimap,
                      zoomState:WorldPanel.ZoomState,
                      mapState:WorldPanel.MapState)
  extends Panel(rect, bgColor) {
  import WorldPanel._
  val (iSpawn, jSpawn) = (0,0)
  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }
  val regionSize = 16
  val miniDiv = world.cols/69//minimap.div //(4096*16)/69
  val regionDiv = 16
  override def update:WorldPanel = {
    val newTooltip = getTooltip
    val newWorld = world.update
    val newZoomState = if (Controls.Enter.justPressed) {
      zoomState.next.getOrElse(zoomState)
    } else if (Controls.Backspace.justPressed) {
      zoomState.prev.getOrElse(zoomState)
    } else {
      zoomState
    }

    val newMapState = if (Controls.Space.justPressed) {
      mapState.next
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

  def getScale:Int = zoomState match {
    case Region => 1
    case Full => regionDiv
    case Mini => miniDiv
  }

  def getTiles:Indexed2d[WorldTile] = zoomState match {
    case Region => world.worldTiles
    case Full => world.worldTiles
    case Mini => world.worldTiles
  }


  private def getTooltip:InfoPanel =  {
    def getTooltip(t:WorldTile):Tooltip = zoomState match {
      case Region => t.regionTooltip
      case Full => t.fullTooltip
      case Mini => t.fullTooltip
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

      mapState match {
        case Nychthemera =>
          val light = Color.fromHsb(t.daylight.toFloat/2)
          t.tile.copy(bgColor = light).draw(i, j)
        case Latitude =>
          t.tile.copy(bgColor = t.latitude.color).draw(i, j)
        case Elevation =>
          t.tile.draw(i, j)
        case Biome =>
          t.tile.copy(bgColor = t.region, code = Code.` `).draw(i, j)
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
    zoomState match {
      case Region =>
        view.draw(world.worldTiles, x, y, drawWorldTile, WorldTile.Blank)
      case Full =>
        view.scaled(regionDiv).draw(world.worldTiles.sample(regionDiv), x, y, drawWorldTile, WorldTile.Blank)
      case Mini =>
        view.scaled(miniDiv).draw(world.worldTiles.sample(miniDiv), x, y, drawWorldTile, WorldTile.Blank)
    }


    tooltip.draw()
    drawName()
    drawDebug()
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
}
