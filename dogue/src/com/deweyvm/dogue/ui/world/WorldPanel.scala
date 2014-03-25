package com.deweyvm.dogue.ui.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.data.{Array2dView, Pointer}
import com.deweyvm.dogue.common.procgen.PerlinNoise
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.world._
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.loading
import com.deweyvm.dogue.common.data.control.{Yield, Coroutine}
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.ui._
import com.deweyvm.dogue.common.procgen.PerlinParams
import com.deweyvm.dogue.world.WorldParams
import com.deweyvm.dogue.world.ArrayViewer
import com.deweyvm.dogue.common.data.control.Return
import com.deweyvm.dogue.world.DateConstants
import com.deweyvm.dogue.ui.WindowMessage.Clear


object WorldPanel {
  val controlsHeight = 8
  val minSideWidth = 24
  val minimapSize = 69
  def computeMapWidth(screenCols:Int) = {
    val maxMinimapPanelSize = minimapSize + 3
    if (screenCols > minSideWidth + maxMinimapPanelSize) {
      maxMinimapPanelSize
    } else {
      screenCols - minSideWidth
    }
  }
  def computeSideWidth(screenCols:Int) = {
    val maxMinimapPanelSize = minimapSize + 3
    if (screenCols > minSideWidth + maxMinimapPanelSize) {
      screenCols - maxMinimapPanelSize
    } else {
      minSideWidth
    }
  }
  def create(rect:Recti,
             minimapSize:Int,
             size:Int,
             world:World,
             params:WorldParams,
             tooltipId:WindowId):WorldPanel = {



    val worldViewer = ArrayViewer(rect.width, rect.height, size/2, size/2, Controls.AxisX, Controls.AxisY)
    new WorldPanel(rect.width, rect.height, world, worldViewer, params.minimapSize, ZoomState.getPointer, MapState.getPointer, tooltipId)
  }


  /**
   * An ad-hoc coroutine for loading everythin in stages
   */
  def getLoaders(screenCols:Int, screenRows:Int, tooltipId:WindowId):Coroutine[WorldPanel] = {
    val seed = 0//System.nanoTime
    val worldSize = 256
    val cols = worldSize
    val rows = worldSize

    val minimapSize = WorldPanel.minimapSize
    val bgColor = Color.Blue
    println("Seed: " + seed + "L")
    val date = DateConstants(framesPerDay = 60*60*24*60)
    val perlin = PerlinParams(worldSize/4, 8, worldSize, seed)
    val worldParams = WorldParams(minimapSize, perlin, date)

    import loading._
    Yield(0, "Loading region data", () => {
    val (altRegions, latRegions, surfaceRegions) = Loads.loadRegionMaps.get
    val latMap = new LatitudeMap(cols, rows, latRegions)
    Yield(12, "Loading biome data", () => {
    val biomes = Loads.loadBiomes(latRegions, altRegions, surfaceRegions).get
    Yield(25, "Generating noise", () => {
    val noise = new PerlinNoise(worldParams.perlin).render
    Yield(34, "Generating surface features", () => {
    val surface = new SurfaceMap(noise, worldParams.perlin, surfaceRegions)
    Yield(50, "Generating wind currents", () => {
    val windMap = new StaticWindMap(surface.heightMap, 10000, 1, seed)
    Yield(62, "Plotting average rainfall", () => {
    val moistureMap = new MoistureMap(surface, latMap.latitude, windMap.arrows, 0.5, cols/2, seed)
    Yield(75, "Choosing biomes", () => {
    val biomeMap = new BiomeMap(moistureMap, surface, latMap, altRegions, biomes)
    Yield(88, "Constructing ecosphere", () => {
    val eco = Ecosphere.buildEcosphere(worldParams, latMap, noise, surface, windMap, moistureMap, biomeMap, surfaceRegions, latRegions, altRegions, Vector())
    val world = World.create(worldParams, eco)
    val mapWidth = WorldPanel.computeMapWidth(screenCols)
    val sideWidth = WorldPanel.computeSideWidth(screenCols)
    val mapRect = Recti(sideWidth + 2, 1, mapWidth - 3, screenRows - 2)

    val worldPanel = WorldPanel.create(mapRect, minimapSize, worldSize, world, worldParams, tooltipId)
    Return(() => {
      worldPanel
    })
    })
    })
    })
    })
    })
    })
    })
    })
  }
}

case class WorldPanel(width:Int, height:Int, world:World, view:ArrayViewer, minimapSize:Int, zoomState:Pointer[ZoomState], mapState:Pointer[MapState], tooltipId:WindowId) extends WindowContents {
  private val (iSpawn, jSpawn) = (0,0)
  private val regionSize = 16
  private val miniDiv = world.cols/minimapSize
  private def mkDebug:String => Text = Text.fromString(Color.Black, Color.White)
  override def outgoing:Map[WindowId, Seq[WindowMessage]] = {
    def getTooltip(t:WorldTile):Tooltip = zoomState.get match {
      case ZoomState.Full => t.regionTooltip
      case ZoomState.Mini => t.fullTooltip
    }
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTooltip(getTiles.get(i, j))
    val msgs = WindowMessage.Clear +: tip.lines.map {s => WindowMessage.TextMessage(s)}
    Map(tooltipId -> msgs)
  }

  override def update(s:Seq[WindowMessage]):(Option[WorldPanel], Seq[Window]) = {
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

    val self = this.copy(world = newWorld,
                         view = newView,
                         zoomState = newZoomState,
                         mapState = newMapState)
    (self.some, Seq())

  }

  def getScale:Int = zoomState.get match {
    case ZoomState.Full => 1
    case ZoomState.Mini => miniDiv
  }

  def getTiles:Array2dView[WorldTile] = zoomState.get match {
    case ZoomState.Full => world.worldTiles
    case ZoomState.Mini => world.worldTiles
  }

  private def drawTiles(r:WindowRenderer):WindowRenderer = {
    val draw: (WorldTile) => Tile = mapState.get.renderTile

    val tiles = world.worldTiles
    r <+| (zoomState.get match {
      case ZoomState.Full =>
        view.draw(tiles, 0, 0, draw)
      case ZoomState.Mini =>
        view.scaled(miniDiv).draw(tiles.sample(miniDiv), 0, 0, draw)
    })
  }


  override def draw(r:WindowRenderer):WindowRenderer = {
    r <++| List(drawTiles, drawName, drawDebug, drawDate)
    //tooltip.draw()
  }

  private def drawDebug(r:WindowRenderer):WindowRenderer = {
    val strs = world.eco.getTimeStrings
    r <++| strs.zipWithIndex.map { case (s, i) =>
      mkDebug(s).draw(0, height + 1 - i) _
    }
  }

  private def drawName(r:WindowRenderer):WindowRenderer = {
    val name = world.worldParams.name
    val xName = (width - name.length)/2
    r <+| mkDebug(name).draw(xName,0)
  }

  private def drawDate(r:WindowRenderer):WindowRenderer = {
    val time = world.cycle.date.getString
    val xName = (width - time.length)/2
    r <+| mkDebug(time).draw(xName, height)
  }
}
