package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.OrthographicCamera
import com.deweyvm.dogue.Game

class Camera {
  private val cam = {
    val c = new OrthographicCamera(1,1)
    c.setToOrtho(true, Game.RenderWidth, Game.RenderHeight)
    c
  }

  def getProjection = {
    cam.update()
    cam.combined
  }
}