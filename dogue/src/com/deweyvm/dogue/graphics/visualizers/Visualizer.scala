package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.graphics.OglRenderer

trait Visualizer {
  def drawShape(ogl:OglRenderer) {

  }

  def drawBatch(ogl:OglRenderer) {

  }

  def zoom = 1.0
  def translation = (0, 0)
}
