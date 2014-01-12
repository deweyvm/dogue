package com.explatcreations.whatever

import util.Random
import com.explatcreations.gleany.Debug

class Globals {
  Debug.load()
  private var drawDebug = false
  var IsDebugMode = true

  var Version = "0.X.X"
  //def isDebugging = true

  def makeGuid: String = "%08x%08x%08x%08x" format (Random.nextInt(),Random.nextInt(),Random.nextInt(),Random.nextInt())

  def shouldDrawDebug: Boolean = drawDebug

  def toggleDrawDebug() {
    drawDebug = !drawDebug
  }
}
