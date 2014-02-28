package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.Texture
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.AssetLoader
import com.deweyvm.gleany.data.{Point2d, Recti}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.deweyvm.dogue.common.data.Code
import com.badlogic.gdx.Gdx
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.graphics.visualizers._

class OglTile(tileset:Tileset) {
  val rows = tileset.rows
  val width = tileset.tileWidth
  val height = tileset.tileHeight
  val texture = tileset.texture
  val rect = new RectSprite(width, height, Color.White)

  private def makeSprite(index:Int, color:Color, texture:Texture) = {
    val x = index % rows
    val y = index / rows
    val region = AssetLoader.makeTextureRegion(texture,Recti(x * width, y * height, width, height).some)
    val sprite = new Sprite(region)
    sprite.setColor(color.toLibgdxColor)
    sprite
  }

  def getSprites(code:Code, fgColor:Color, bgColor:Color) = {
    val fg = makeSprite(code.index, fgColor, texture)
    val bg = new RectSprite(width, height, bgColor).sprite
    (fg, bg)
  }


}

class OglRenderer(tileset:Tileset) extends Renderer {
//  val vis:Option[Visualizer] = None
  val vis:Option[Visualizer] = new VectorFieldVisualizer().some
  //val vis:Option[Visualizer] = new HexGridVisualizer().some
  //val vis:Option[Visualizer] = new PerlinVisualizer().some
  //val vis:Option[Visualizer] = new VoronoiVisualizer().some
//  val vis:Option[Visualizer] = new PoissonVisualizer().some
  private val width = tileset.tileWidth
  private val height = tileset.tileHeight
  private val oglTile = new OglTile(tileset)

  val batch = new SpriteBatch
  val shape = new ShapeRenderer
  val camera = new Camera

  private val draws = ArrayBuffer[() => Unit]()

  def drawSprite(s:Sprite, x:Float, y:Float) {
    draws.append(() => {
      s.setPosition(x, y)
      s.draw(batch)
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
    drawTileRaw(t, i*width, j*height)
  }

  def drawTileRaw(t:Tile, x:Double, y:Double) {
    val (fg, bg) = oglTile.getSprites(t.code, t.fgColor, t.bgColor)
    drawSprite(bg, x.toFloat, y.toFloat)
    drawSprite(fg, x.toFloat, y.toFloat)
  }

  override def render() {
    Gdx.gl.glClearColor(0,0,0,1)
    batch.begin()
    vis foreach {v =>
      camera.zoom(v.zoom)
      (camera.translate _).tupled(v.translation)
    }
    batch.setProjectionMatrix(camera.getProjection)
    vis foreach {_.drawBatch(this)}
    draws foreach {_()}
    vis foreach {v =>
      camera.zoom(1)
      camera.translate(-v.translation._1, -v.translation._2)
    }
    draws.clear()
    batch.end()
    vis foreach {_.drawShape(this)}
  }

}
