package com.deweyvm.dogue

import com.deweyvm.gleany.{Glean, GleanyInitializer, GleanyGame}
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.threading.ThreadManager
import com.deweyvm.dogue.loading.{DogueSettings, RawDogueSettings}
import com.deweyvm.dogue.common.logging.Log
import com.badlogic.gdx.Gdx
import java.util.concurrent.{TimeUnit, Callable, Executors}
import java.util

object Game {
  val Zoom = 1
  private val factor = 1
  val Width = 32*32*factor
  val Height = 32*9*factor
  val RenderWidth = Width/Zoom
  val RenderHeight = Height/Zoom
  val globals = new Globals
  val settings = DogueSettings.load()
  DogueSettings.flush()
  val fps = 60

  private var frame = 0
  def getFrame = frame

  def shutdown() {
    //cleanup()
    Gdx.app.exit()
  }

  private def cleanup() {
    Log.info("Closing game")
    Log.flush()
    Client.instance.kill()
    Client.instance.disconnect(Client.State.Closed)
  }
}

class Game(initializer: GleanyInitializer) extends GleanyGame(initializer) {
  import Game._
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

  override def dispose() {
    cleanup()
  }
}
