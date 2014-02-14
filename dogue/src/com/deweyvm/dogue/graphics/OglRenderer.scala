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
import com.deweyvm.dogue.common.procgen.voronoi.Voronoi
import scala.util.Random
import com.deweyvm.dogue.common.procgen._
import com.deweyvm.gleany.data.Rectd
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.Game

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

object OglRenderer {
  val vorSize = 300
  val vorScale = 30
  val vorSeed = 1L
}

class OglRenderer(tileset:Tileset) extends Renderer {
  import OglRenderer._
  val vectorField = VectorField.windSpiral(Game.RenderWidth, Game.RenderHeight, 40)
  val r = new Random()
  val size = vorSize
  val scale = vorScale
  val pts = new PoissonRng(size, size, {case (i, j) => scale}, scale, vorSeed).getPoints
  val edges = Voronoi.getEdges(pts, size, size)
  val polys = Voronoi.getFaces(edges, Rectd(0, 0, size, size)) map { p:Polygon =>
    val mapped = p.lines map { _.p }
    flattenVector(mapped)
  }
  val colors = polys map {_ => Color.randomHue()}
  private val width = tileset.tileWidth
  private val height = tileset.tileHeight
  private val oglTile = new OglTile(tileset)
  private val batch = new SpriteBatch
  private val shape = new ShapeRenderer
  private val camera = new Camera
  private val draws = ArrayBuffer[() => Unit]()

  def draw(s:Sprite, x:Float, y:Float) {
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

  def drawPoint(pt:Point2d, color:Color) {
    shape.begin(ShapeType.Filled)
    shape.setColor(color.toLibgdxColor)
    shape.circle(pt.x.toFloat, pt.y.toFloat, 4)
    shape.end()
  }

  def drawVectorField(v:VectorField, color:Color) {

    shape.setColor(color.toLibgdxColor)
    v.vectors foreach {case (pt, arrow) =>
      val (line, (l1, l2, l3)) = arrow.getShapes(pt)
      shape.begin(ShapeType.Line)
      shape.line(line.p.x.toFloat, line.p.y.toFloat, line.q.x.toFloat, line.q.y.toFloat)
      shape.end()
      shape.begin(ShapeType.Filled)
      shape.triangle(
        l1.p.x.toFloat,
        l1.p.y.toFloat,
        l2.q.x.toFloat,
        l2.q.y.toFloat,
        l3.p.x.toFloat,
        l3.p.y.toFloat
      )
      shape.end()

    }


  }

  def flattenVector(pts:Vector[Point2d]):Array[Float] = {
    val flat = pts.foldRight(Vector[Float]()){ case (p, acc) =>
      p.x.toFloat +: (p.y.toFloat +: acc)
    }
    Array(flat:_*)
  }


  def drawPolygon(pts:Array[Float], color:Color) {
    shape.begin(ShapeType.Line)
    shape.setColor(color.toLibgdxColor)
    shape.polygon(pts)
    shape.end()
  }

  def drawRect(x:Int, y:Int, width:Int, height:Int, color:Color) {
    shape.begin(ShapeType.Filled)
    shape.setColor(color.toLibgdxColor)
    shape.rect(x, y, width, height)
    shape.end()
  }

  override def draw(t:Tile, i:Int, j:Int) {
    val (fg, bg) = oglTile.getSprites(t.code, t.fgColor, t.bgColor)
    draw(bg, i*width, j*height)
    draw(fg, i*width, j*height)
  }

  override def render() {
    Gdx.gl.glClearColor(0,0,0,1)
    batch.begin()
    batch.setProjectionMatrix(camera.getProjection)
    draws foreach {_()}
    draws.clear()
    batch.end()
    camera.zoom(2)
    camera.translate(-Game.RenderWidth/2,-Game.RenderHeight/2)
    shape.setProjectionMatrix(camera.getProjection)
    drawVectorField(vectorField, Color.White)
    camera.translate(Game.RenderWidth/2,Game.RenderHeight/2)

    camera.zoom(1)
    /*camera.translate(-100,-30)
    shape.setProjectionMatrix(camera.getProjection)
    drawRect(0,0,size,size, Color.Black)
    edges foreach { e =>
      //drawLine(e.vorStart, e.vorEnd, Color.White)
      //drawLine(e.triStart, e.triEnd, Color.Green)
      drawPoint(e.triStart, Color.Red)
      drawPoint(e.triEnd, Color.Red)
    }
    polys.zip(colors) foreach { case (p, c) => ()
      drawPolygon(p, c)
    }

    camera.translate(100,30)*/

  }

}
