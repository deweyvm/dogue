package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.Texture
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.AssetLoader
import com.deweyvm.gleany.data.Recti
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.deweyvm.dogue.common.data.Code
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.entities.Tile
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.dogue.common.Implicits
import Implicits._

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

  private val width = tileset.tileWidth
  private val height = tileset.tileHeight
  private val oglTile = new OglTile(tileset)
  private val batch = new SpriteBatch
  private val camera = new Camera
  private val draws = ArrayBuffer[() => Unit]()

  private def draw(s:Sprite, x:Float, y:Float) {
    draws += (() => {
      s.setPosition(x, y)
      s.draw(batch)
    })
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
  }

}
