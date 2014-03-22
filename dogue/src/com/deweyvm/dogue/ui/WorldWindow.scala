package com.deweyvm.dogue.ui


import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.{Timer, Recti}
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.data.{Array2dView, Pointer, Code}
import com.deweyvm.dogue.common.procgen.{PerlinParams, PerlinNoise, VectorField}
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.world._
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.world.WorldParams
import com.deweyvm.dogue.world.ArrayViewer
import com.deweyvm.dogue.loading
import com.deweyvm.dogue.common.data.control.{Return, Yield, Coroutine}

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
      Tile(code, /*t.tile.bgColor*/VectorField.magToColor(magnitude, maxWind), Color.White).draw(i, j)
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
      val color = t.biomeColor
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
object WorldWindow {
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
  /*def create(rect:Recti,
             minimapSize:Int,
             bgColor:Color,
             size:Int,
             world:World,
             params:WorldParams):WorldWindow = {



    val worldViewer = ArrayViewer(rect.width, rect.height, size/2, size/2, Controls.AxisX, Controls.AxisY)
    new WorldWindow(rect, bgColor, world, worldViewer, params.minimapSize, ZoomState.getPointer, MapState.getPointer)
  }


  /**
   * An ad-hoc coroutine for loading everythin in stages
   */
  def getLoaders(screenCols:Int, screenRows:Int):Coroutine[WorldWindow] = {
    val seed = 0//System.nanoTime
    val worldSize = 256
    val cols = worldSize
    val rows = worldSize

    val minimapSize = WorldWindow.minimapSize
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
    val mapWidth = WorldWindow.computeMapWidth(screenCols)
    val sideWidth = WorldWindow.computeSideWidth(screenCols)
    val mapRect = Recti(sideWidth + 2, 1, mapWidth - 3, screenRows - 2)

    val worldPanel = WorldWindow.create(mapRect, minimapSize, bgColor, worldSize, world, worldParams)
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
  }*/
}

/*case class WorldWindow(override val rect:Recti,
                      bgColor:Color,
                      world:World,
                      view:ArrayViewer,
                      minimapSize:Int,
                      zoomState:Pointer[ZoomState],
                      mapState:Pointer[MapState])
  extends Window(rect, bgColor) {
  val (iSpawn, jSpawn) = (0,0)

  val regionSize = 16
  val miniDiv = world.cols/minimapSize
  override def update:WorldWindow = {
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
              //tooltip = newTooltip.update,
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


  /*private def getTooltip:InfoWindow =  {
    def getTooltip(t:WorldTile):Tooltip = zoomState.get match {
      case ZoomState.Full => t.regionTooltip
      case ZoomState.Mini => t.fullTooltip
    }
    val fresh = InfoWindow.makeNew(Recti(1, 1, tooltip.width, tooltip.height), bgColor)
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTooltip(getTiles.get(i, j)).some
    tip.foldLeft(fresh){ case (acc, tt) =>
      acc.addLines(tt.lines, bgColor, tt.color)
    }
  }*/



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


  override def drawBackground() {
    super.drawBackground()
    drawTiles()
    //tooltip.draw()
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
}*/
