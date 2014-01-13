package com.explatcreations.whatever

import com.explatcreations.gleany.{Glean, GleanyInitializer, GleanyGame}
object Game {
  val Zoom = 4
  val Width = 800
  val Height = 800
  val RenderWidth = Width/Zoom
  val RenderHeight = Height/Zoom
}

class Game(initializer: GleanyInitializer) extends GleanyGame(initializer) {
  private lazy val engine = new Engine()

  override def update() {
    engine.update()
  }

  override def draw() {
    engine.draw()
  }

  override def resize(width: Int, height: Int) {
    Glean.y.settings.setWindowSize(width, height)
  }
}