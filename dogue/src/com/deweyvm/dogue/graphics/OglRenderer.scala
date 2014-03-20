package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.Texture
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Point2d
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch, Sprite}
import com.deweyvm.dogue.common.data.Code
import com.badlogic.gdx.Gdx
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.graphics.visualizers._
import com.deweyvm.dogue.Game

case class OglSprite(t:TextureRegion, color:Color) {
  def draw(batch:SpriteBatch, x:Int, y:Int) {
    batch.setColor(color.toLibgdxColor)
    batch.draw(t, x, y)
  }
}

class OglTile(tileset:Tileset) {
  val rows = tileset.rows
  val width = tileset.tileWidth
  val height = tileset.tileHeight
  val texture = tileset.texture

  private def makeSprite(index:Int, color:Color, texture:Texture) = {
    val x = index % rows
    val y = index / rows
    val region = tileset.getRegion(x, y)
    OglSprite(region, color)
  }

  def getSprites(code:Code, fgColor:Color, bgColor:Color) = {
    val fg = makeSprite(code.index, fgColor, texture)
    val bg = makeSprite(Code.█.index, bgColor, texture)
    (fg, bg)
  }
}



class OglRenderer(tileset:Tileset) extends Renderer {
  val vis:Option[Visualizer] = None
  //val vis:Option[Visualizer] = new PerlinVisualizer().some
  //val vis:Option[Visualizer] = new VectorFieldVisualizer().some
  //val vis:Option[Visualizer] = new HexGridVisualizer().some
  //val vis:Option[Visualizer] = new VoronoiVisualizer().some
  //val vis:Option[Visualizer] = new PoissonVisualizer().some
  private val width = tileset.tileWidth
  private val height = tileset.tileHeight
  private val oglTile = new OglTile(tileset)

  val batch = new SpriteBatch
  val shape = new ShapeRenderer
  val scene = new Scene(Game.Width/width, Game.Height/height)
  val camera = new Camera(Game.RenderWidth, Game.RenderHeight)

  private val draws = ArrayBuffer[() => Unit]()

  def drawSprite(s:Sprite, x:Int, y:Int) {
    draws.append(() => {
      s.setPosition(x, y)
      s.draw(batch)
    })
  }

  def drawOglSprite(s:OglSprite, x:Int, y:Int) {
    draws.append(() => {
      s.draw(batch, x, y)
    })
  }

  def drawLine(pt:Point2d, pr:Point2d, color:Color) {
    shape.begin(ShapeType.Line)
    shape.setColor(color.toLibgdxColor)
    shape.line(pt.x.toFloat, pt.y.toFloat, pr.x.toFloat, pr.y.toFloat)
    shape.end()

  }

  def drawPoint(pt:Point2d, color:Color, size:Int=2) {
    shape.begin(ShapeType.Filled)
    shape.setColor(color.toLibgdxColor)
    shape.circle(pt.x.toFloat, pt.y.toFloat, size)
    shape.end()
  }

  def drawPolygonFloat(pts:Array[Float], color:Color) {
    shape.begin(ShapeType.Line)
    shape.setColor(color.toLibgdxColor)
    shape.polygon(pts)
    shape.end()
  }

  def drawPolygon(poly:Polygon, color:Color) {
    poly.lines foreach { line =>
      drawLine(line.p, line.q, color)
    }
  }

  def drawRect(x:Int, y:Int, width:Int, height:Int, color:Color) {
    shape.begin(ShapeType.Filled)
    shape.setColor(color.toLibgdxColor)
    shape.rect(x, y, width, height)
    shape.end()
  }

  def translateShape(x:Int, y:Int)(f:() => Unit) {
    camera.translate(-x, -y)
    shape.setProjectionMatrix(camera.getProjection)
    f()
    camera.translate(x, y)
  }

  override def draw(t:Tile, i:Int, j:Int) {
    scene.set(i, j, t)
  }

  def drawTileRaw(t:Tile, x:Double, y:Double) {
    val (fg, bg) = oglTile.getSprites(t.code, t.fgColor, t.bgColor)
    drawOglSprite(bg, x.toInt, y.toInt)
    drawOglSprite(fg, x.toInt, y.toInt)
  }

  private def drawVisualization() {
    vis foreach { v =>

      batch.begin()
      camera.zoom(v.zoom)
      (camera.translate _).tupled(v.translation)
      batch.setProjectionMatrix(camera.getProjection)
      v.drawBatch(this)
      draws foreach {_()}
      draws.clear()
      camera.zoom(1)
      camera.translate(-v.translation._1, -v.translation._2)
      batch.end()
      v.drawShape(this)
    }

  }
  override def render() {
    Gdx.gl.glClearColor(0,0,0,1)
    batch.begin()
    batch.setProjectionMatrix(camera.getProjection)
    scene.foreach {case(i, j, t) =>
      drawTileRaw(t, i*width, j*height)
    }
    draws foreach {_()}
    draws.clear()
    batch.end()
    drawVisualization()
  }

}
