package com.deweyvm.whatever

import com.deweyvm.gleany.{Glean, GleanyInitializer, GleanyGame}
import com.deweyvm.whatever.net.Client
import com.deweyvm.gleany.net.ThreadManager

object Game {
  val Zoom = 1
  private val factor = 1
  val Width = 32*32*factor
  val Height = 32*9*factor
  val RenderWidth = Width/Zoom
  val RenderHeight = Height/Zoom
  val globals = new Globals
  val client = ThreadManager.spawn(new Client())

  private var frame = 0
  def getFrame = frame
}

class Game(initializer: GleanyInitializer) extends GleanyGame(initializer) {
  private lazy val engine = new Engine()

  override def update() {
    engine.update()
    Game.frame += 1
  }

  override def draw() {
    engine.draw()
  }

  override def resize(width: Int, height: Int) {
    Glean.y.settings.setWindowSize(width, height)
  }
}
