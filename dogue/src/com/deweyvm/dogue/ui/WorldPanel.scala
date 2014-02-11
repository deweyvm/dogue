package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.common.data.Indexed2d
import com.deweyvm.dogue.common.procgen.PerlinPoisson
import com.badlogic.gdx.graphics.{Texture, Pixmap}
import com.badlogic.gdx.graphics.g2d.Sprite

object WorldPanel {
  trait State {
    def next:Option[State]
    def prev:Option[State]
  }

  case object Region extends State {
    override def next = None
    override def prev = Full.some
  }
  case object Full extends State {
    override def next = Region.some
    override def prev = Mini.some
  }
  case object Mini extends State {
    override def next = Full.some
    override def prev = None
  }

  def create(x:Int, y:Int, width:Int, height:Int,
             tooltipWidth:Int, tooltipHeight:Int,
             bgColor:Color, size:Int):WorldPanel = {
    val world = new World(WorldParams(size/4, 22, size))
    val tooltip = InfoPanel.makeNew(1, 1, tooltipWidth, tooltipHeight, bgColor)
    val minimap = new Minimap(world, 69)
    val worldViewer = ArrayViewer(width, height, 0, 0, Controls.AxisX, Controls.AxisY)
    new WorldPanel(x, y, width, height, bgColor, world, worldViewer, tooltip, minimap, Mini)
  }
}




case class WorldPanel(override val x:Int,
                      override val y:Int,
                      override val width:Int,
                      override val height:Int,
                      bgColor:Color,
                      world:World,
                      view:ArrayViewer,
                      tooltip:InfoPanel,
                      minimap:Minimap,
                      state:WorldPanel.State)
  extends Panel(x, y, width, height, bgColor) {
  import WorldPanel._
  val (iSpawn, jSpawn) = (0,0)
  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }
  val regionSize = 16
  val miniDiv = (4096*16)/69
  val regionDiv = 16
  override def update:WorldPanel = {
    val newTooltip = getTooltip
    val newWorld = world.update
    val newState = if (Controls.Enter.justPressed) {
      state.next.getOrElse(state)
    } else if (Controls.Backspace.justPressed) {
      state.prev.getOrElse(state)
    } else {
      state
    }

    val newView = view.update(getTiles, getScale)

    this.copy(world = newWorld,
              view = newView,
              tooltip = newTooltip.update,
              state = newState)

  }

  def getScale:Int = state match {
    case Region => 1
    case Full => regionDiv
    case Mini => miniDiv
  }

  def getTiles:Indexed2d[WorldTile] = state match {
    case Region => world.tiles
    case Full => world.tiles
    case Mini => world.tiles
  }


  private def getTooltip:InfoPanel =  {
    def getTooltip(t:WorldTile):Tooltip = state match {
      case Region => t.regionTooltip
      case Full => t.fullTooltip
      case Mini => t.fullTooltip
    }
    val fresh = InfoPanel.makeNew(1, 1, tooltip.width, tooltip.height, bgColor)
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTiles.get(i, j) map getTooltip
    tip.foldLeft(fresh){ case (acc, tt) =>
      acc.addLines(tt.lines, bgColor, tt.color)
    }
  }

  override def draw() {
    super.draw()
    def drawWorldTile(i:Int, j:Int, t:WorldTile) = {
      t.tile.draw(i, j)
    }
    state match {
      case Region =>
        view.draw(world.tiles, x, y, drawWorldTile)
      case Full =>
        view.scaled(regionDiv).draw(world.tiles.sample(regionDiv), x, y, drawWorldTile)
      case Mini =>
        view.scaled(miniDiv).draw(minimap.sampled, x, y, drawWorldTile)
    }


    tooltip.draw()
    drawName()
    //
  }

  private def drawName() {
    val name = world.worldParams.name
    val xName = x + (width - name.length)/2
    new Text(name, Color.Black, Color.White).draw(xName,0)
  }
}
