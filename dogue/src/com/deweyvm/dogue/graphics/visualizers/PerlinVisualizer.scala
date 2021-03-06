package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.gleany.graphics.ImageUtils
import com.deweyvm.dogue.graphics.OglRenderer
import com.badlogic.gdx.graphics.g2d.Sprite
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.world.TopoFeature
import com.deweyvm.dogue.common.procgen.PerlinParams

class PerlinVisualizer extends Visualizer {
  override def zoom = 0.5
  override def translation = (-Game.RenderWidth/4, -Game.RenderHeight/4)
  val size = 256
  val perlin = PerlinParams(16, 6, size, System.nanoTime)
  val mountains = TopoFeature.create(TopoFeature.lake, 50, perlin)
  val texture = ImageUtils.makeColorTexture(mountains.getNoise(false).toVector, size, size)
  override def drawBatch(ogl:OglRenderer) = {
    ogl.drawSprite(new Sprite(texture), 10, 10)
  }
}

