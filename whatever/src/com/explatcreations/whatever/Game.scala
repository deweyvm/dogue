package com.explatcreations.whatever

import com.explatcreations.gleany.{Glean, GleanyInitializer, GleanyGame}

class Game(initializer: GleanyInitializer) extends GleanyGame(initializer) {
  //private lazy val engine = new Engine()

  private var paused = false

  override def update() {
    /*if (Controls.Quit.justPressed) {
      GleanyGame.exit()
    }
    if (Controls.Pause.justPressed) {
      paused = !paused
    }*/

    if (!paused) {
      //engine.update()
    } else {
      //Controls.update()
    }
  }

  override def draw() {
    //engine.draw()
  }

  override def resize(width: Int, height: Int) {
    Glean.y.settings.setWindowSize(width, height)
  }
}