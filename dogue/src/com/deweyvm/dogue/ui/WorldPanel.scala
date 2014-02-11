package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.common.data.Indexed2d
import com.deweyvm.dogue.common.procgen.{PerlinNoise, PoissonRng}
import com.badlogic.gdx.graphics.{Texture, Pixmap}
import com.badlogic.gdx.graphics.g2d.Sprite

object WorldPanel {
  trait State {
    def next:Option[State]
    def prev:Option[State]
  }



  def create(iSpawn:Int, jSpawn:Int,
             x:Int, y:Int, width:Int, height:Int,
             tooltipWidth:Int, tooltipHeight:Int,
             bgColor:Color, cols:Int, rows:Int):WorldPanel = {
    val world = new World(WorldParams.default)
    val tooltip = InfoPanel.makeNew(1, 1, tooltipWidth, tooltipHeight, bgColor)
    val minimap = new Minimap(world, 64)
    val worldViewer = ArrayViewer(width, height, 0, 0, Controls.AxisX, Controls.AxisY)
    new WorldPanel(x, y, width, height, bgColor, world, worldViewer, tooltip, minimap, Mini)
  }

  val texture = {
    val size = 512
    val perlin = new PerlinNoise(1/32.0, 5, size, 50).render
    println("Perlin rendererd")
    val min = 2
    val rng = new PoissonRng(size,size, {
      case (i, j) => perlin.get(i, j).map{t =>
        val x = t*35
        //println(x)
        math.max(x, min)
      }.getOrElse(10)
    }, min)
    val pixmap = new Pixmap(size,size, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.Green.toLibgdxColor)
    pixmap.fill()
    rng.getPoints foreach { p =>
      pixmap.setColor(Color.DarkGreen.toLibgdxColor)
      pixmap.drawPixel(p.x.toInt, p.y.toInt)
      pixmap.drawPixel(p.x.toInt+1, p.y.toInt)
      pixmap.drawPixel(p.x.toInt, p.y.toInt+1)
      pixmap.drawPixel(p.x.toInt+1, p.y.toInt+1)
    }
    val t = new Texture(pixmap)
    pixmap.dispose()
    new Sprite(t)
  }
}

case object Full extends WorldPanel.State {
  override def next = None
  override def prev = Mini.some
}
case object Mini extends WorldPanel.State {
  override def next = Full.some
  override def prev = None
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
  val (iSpawn, jSpawn) = (0,0)
  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }
  val div = world.cols/minimap.sampled.cols
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
    case Full => 1
    case Mini => div
  }

  def getTiles:Indexed2d[WorldTile] = state match {
    case Full => world.tiles
    case Mini => world.tiles//minimap.sampled
  }

  private def getTooltip:InfoPanel = {
    val fresh = InfoPanel.makeNew(1, 1, tooltip.width, tooltip.height, bgColor)
    val (i, j) = (view.xCursor, view.yCursor)
    val tip = getTiles.get(i, j) map {_.tooltip}
    tip.foldLeft(fresh){ case (acc, (tColor, lines)) =>
      acc.addLines(lines, bgColor, tColor)
    }
  }

  override def draw() {
    super.draw()
    def drawWorldTile(i:Int, j:Int, t:WorldTile) = {
      t.tile.draw(i, j)
    }
    state match {
      case Full =>
        view.draw(world.tiles, x, y, drawWorldTile)
      case Mini =>
        view.withCursor(view.xCursor/div, view.yCursor/div).draw(minimap.sampled, x, y, drawWorldTile)
    }


    tooltip.draw()
    //
  }
}
