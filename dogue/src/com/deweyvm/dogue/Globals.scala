package com.deweyvm.dogue

import util.Random
import com.deweyvm.gleany.Debug
import com.deweyvm.dogue.common.Implicits._

class Globals {
  Debug.load()
  private var drawDebug = false
  var IsDebugMode = true

  var Version = "0.X.X"

  def shouldDrawDebug: Boolean = drawDebug

  def toggleDrawDebug() {
    drawDebug = !drawDebug
  }
}
