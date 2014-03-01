package com.deweyvm.dogue

import com.deweyvm.gleany.{Glean, GleanyInitializer, GleanyGame}
import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.threading.ThreadManager
import com.deweyvm.dogue.loading.{DogueSettings, RawDogueSettings}
import com.deweyvm.dogue.common.logging.Log
import com.badlogic.gdx.Gdx
import java.util.concurrent.{TimeUnit, Callable, Executors}
import java.util
import com.deweyvm.dogue.common.protocol.{DogueOps, Command}

object Game {
  val Zoom = 1
  private val factor = 2
  val Width = 32*16*factor// + 16*40
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
    Dogue.gdxApp foreach {_.exit()}
    System.exit(0)
  }

  private def cleanup() {
    Log.info("Closing game")
    Log.flush()
    Client.instance.disconnect(Client.State.Closed)
    Client.instance.kill()
  }
}

class Game(initializer: GleanyInitializer) extends GleanyGame(initializer) {
  import Game._
  private lazy val engine = new Engine()


  override def render() {
    try {
      super.render()
    } catch {
      case t:Throwable =>
        dispose()
        throw t
    }

  }

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
    val executor = Executors.newSingleThreadExecutor()
    executor.invokeAll(util.Arrays.asList(new Callable[Unit] {
      override def call(): Unit = cleanup()
    }), 2, TimeUnit.SECONDS)
    executor.shutdown()
  }
}
