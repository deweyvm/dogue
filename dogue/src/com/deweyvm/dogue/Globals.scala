package com.deweyvm.dogue

import com.deweyvm.gleany.Debug

class Globals {
  Debug.load()
  private var drawDebug = false
  var IsDebugMode = true
  var IsHeadless = false
  var Version = "0.X.X"

  def shouldDrawDebug: Boolean = drawDebug

  def toggleDrawDebug() {
    drawDebug = !drawDebug
  }
}
