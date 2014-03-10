package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.gleany.graphics.ImageUtils
import com.deweyvm.dogue.graphics.OglRenderer
import com.badlogic.gdx.graphics.g2d.Sprite
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.world.TopoFeature

class PerlinVisualizer extends Visualizer {
  override def zoom = 0.5
  override def translation = (-Game.RenderWidth/4, -Game.RenderHeight/4)
  val size = 256
  val mountains = new TopoFeature(TopoFeature.lake, 50, size, 16, 6, System.nanoTime)
  val texture = ImageUtils.makeColorTexture(mountains.getNoise.toVector, size, size)
  override def drawBatch(ogl:OglRenderer) = {
    ogl.drawSprite(new Sprite(texture), 10, 10)
  }
}

