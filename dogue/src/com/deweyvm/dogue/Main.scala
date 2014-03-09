package com.deweyvm.dogue

import com.deweyvm.gleany.{GleanyInitializer, GleanyConfig, GleanyGame}
import com.deweyvm.gleany.files.PathResolver
import com.deweyvm.gleany.saving.Settings
import com.deweyvm.dogue.input.DogueControls
import com.deweyvm.dogue.loading.DogueDefaultSettings
import com.deweyvm.dogue.common.logging.{LogLevel, Log}
import com.deweyvm.dogue.common.testing.TestManager


object Main {
  def main(args: Array[String]) {
    val c = OptParser.getOptions(args)
    //TestManager.runAll(c.failFirst)
    if (c.testsOnly) {
      sys.exit(0)
    }
    val s = Game.settings
    val logLevel = LogLevel.fromString(c.logLevel)
    Log.initLog(s.logLocation.get, logLevel)
    Game.globals.IsDebugMode = c.isDebug
    Game.globals.IsHeadless = c.headless
    if (c.version) {
      println(Game.globals.Version)
      sys.exit(0)
    }

    val iconPath = "sprites/icon.gif"
    val settings = new Settings(DogueControls, DogueDefaultSettings)
    settings.raw.width = s.width.get*s.tileSize.get
    settings.raw.height = s.height.get*s.tileSize.get
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
  }
}
