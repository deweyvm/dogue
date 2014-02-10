package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world.{WorldTile, WorldParams, World, WorldViewer}
import com.deweyvm.gleany.graphics.{ImageUtils, Color}
import com.deweyvm.gleany.data.Recti
import com.badlogic.gdx.graphics.Texture
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.Dogue
import com.deweyvm.dogue.graphics.OglRenderer
import com.badlogic.gdx.graphics.g2d.Sprite
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.data.Indexed2d

object WorldPanel {
  def create(iSpawn:Int, jSpawn:Int,
             x:Int, y:Int, width:Int, height:Int,
             tooltipWidth:Int, tooltipHeight:Int,
             bgColor:Color, cols:Int, rows:Int):WorldPanel = {
    val view = WorldViewer.create(iSpawn, jSpawn, width, height)
    val grid = new World(WorldParams.default)
    val tooltip = InfoPanel.makeNew(1, 1, tooltipWidth, tooltipHeight, bgColor)
    new WorldPanel(x, y, width, height, bgColor, grid, view, tooltip)
  }

  var mapTiles:Option[(Vector[WorldTile], Int, Int)] = None
  var mapTexture:Option[Texture] = None
  def newMap() {
    new Thread(new Runnable {
      override def run(): Unit = {
        WorldPanel.mapTexture foreach {_.dispose()}
        WorldPanel.mapTexture = None

        val world = new World(WorldParams.default.copy(size = 512))
        val tiles = world.tiles.strictGetAll
        mapTiles = (tiles, world.cols, world.rows).some

      }
    }).start()
  }
  newMap()
}


case class WorldPanel(override val x:Int,
                      override val y:Int,
                      override val width:Int,
                      override val height:Int,
                      bgColor:Color,
                      grid:World,
                      view:WorldViewer,
                      tooltip:InfoPanel)
  extends Panel(x, y, width, height, bgColor) {
  val (iSpawn, jSpawn) = (0,0)

  override def getRects:Vector[Recti] = {
    super.getRects ++ tooltip.getRects
  }

  override def update:WorldPanel = {
    val newView = view.update(grid.cols - width - 1, grid.rows - height - 1)
    val newTooltip = getTooltip
    val newGrid = grid.update
    if (Controls.Backspace.justPressed) {
      WorldPanel.newMap()
    }
    this.copy(grid = newGrid, view = newView, tooltip = newTooltip.update)

  }

  private def getTooltip:InfoPanel = {
    val fresh = InfoPanel.makeNew(1, 1, tooltip.width, tooltip.height, bgColor)
    view.getTooltip(grid).foldLeft(fresh){ case (acc, (tColor, lines)) =>
      acc.addLines(lines, bgColor, tColor)
    }
  }

  override def draw() {
    super.draw()
    view.draw(grid, x, y)
    tooltip.draw()
    WorldPanel.mapTiles foreach {case (t, c, r) =>
      val texture = ImageUtils.makeTexture(t.map {_.height}, c, r)
      WorldPanel.mapTexture = texture.some
      WorldPanel.mapTiles = None
      Log.info("DONE")
    }
    WorldPanel.mapTexture foreach {t =>
      Dogue.renderer.asInstanceOf[OglRenderer].draw(new Sprite(t), 0, 0)
    }
  }
}
