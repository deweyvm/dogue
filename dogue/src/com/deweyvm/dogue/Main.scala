package com.deweyvm.dogue

import com.deweyvm.gleany.{GleanyInitializer, GleanyConfig, GleanyGame}
import com.deweyvm.gleany.files.PathResolver
import com.deweyvm.gleany.saving.Settings
import com.deweyvm.dogue.input.DogueControls
import com.deweyvm.dogue.loading.DogueDefaultSettings
import com.deweyvm.dogue.common.logging.Log
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.common.testing.TestManager
import com.deweyvm.gleany.graphics.ImageUtils
import com.deweyvm.dogue.common.procgen.{NaiveVoronoi, PerlinNoise}
import com.deweyvm.gleany.data.Time


object Main {
  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[DogueOptions]("dogue") {
      head("dogue", Game.globals.Version)

      opt[Unit]("test-only") action { (_, c) =>
        c.copy(testsOnly = true)
      } text "run tests then exit"

      opt[Unit]("fail-first") action { (_, c) =>
        c.copy(failFirst = true)
      } text "stop testing after the first failure"

      opt[Unit]("debug") action { (_, c) =>
        c.copy(isDebug = true)
      } text "run in debug mode"

      opt[Unit]("version") action { (_, c) =>
        c.copy(version = true)
      } text "show version"

      opt[Unit]("headless") action { (_, c) =>
        c.copy(headless = true)
      }
    }

    parser.parse(args, DogueOptions()) map { c =>
      TestManager.runAll(c.failFirst)
      if (c.testsOnly) {
        sys.exit(0)
      }
      val s = Game.settings
      Log.initLog(s.logLocation.get, Log.Warn)
      Game.globals.IsDebugMode = c.isDebug
      Game.globals.IsHeadless = c.headless
      if (c.version) {
        println(Game.globals.Version)
        sys.exit(0)
      }

      val iconPath = "sprites/icon.gif"
      val settings = new Settings(DogueControls, DogueDefaultSettings)
      val config = new GleanyConfig(settings, "Dogue", Some(iconPath))
      val pathResolver = new PathResolver(
        "fonts",
        "sprites",
        "sfx",
        "music",
        "shaders",
        "maps"
      )
      val initializer = new GleanyInitializer(pathResolver, settings)
      if (!c.headless) {
        GleanyGame.runGame(config, new Game(initializer))
      } else {
        Dogue.behead()
        val game = new Engine()
        while (true) {
          game.update()
          game.draw()
          Thread.sleep(16)
        }
      }
    } getOrElse {
      println(parser.usage)
      Dogue.gdxApp foreach {_.exit()}
    }



  }
}
