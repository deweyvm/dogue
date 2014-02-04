package com.deweyvm.dogue

import com.deweyvm.gleany.{GleanyInitializer, GleanyConfig, GleanyGame}
import com.deweyvm.gleany.files.PathResolver
import com.deweyvm.gleany.saving.Settings
import com.deweyvm.dogue.input.WhateverControls
import com.deweyvm.dogue.loading.WhateverDefaultSettings
import com.deweyvm.dogue.common.logging.Log
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.common.testing.TestManager


object Main {
  def main(args: Array[String]) {
    TestManager.runAll(true)
    val parser = new scopt.OptionParser[DogueOptions]("dogue") {
      head("dogue", Game.globals.Version)

      opt[Unit]("debug") action { (_, c) =>
        c.copy(isDebug = true)
      } text "run in debug mode"

      opt[Unit]("version") action { (_, c) =>
        c.copy(version = true)
      } text "show version"

    }
    parser.parse(args, DogueOptions()) map { c =>
      val s = Game.settings
      Log.initLog(s.logLocation.get, Log.All)
      Game.globals.IsDebugMode = c.isDebug
      if (c.version) {
        println(Game.globals.Version)
        sys.exit(0)
      }

      val iconPath = "sprites/icon.gif"
      val settings = new Settings(WhateverControls, WhateverDefaultSettings)
      val config = new GleanyConfig(settings, "Whatever", Some(iconPath))
      val pathResolver = new PathResolver(
        "fonts",
        "sprites",
        "sfx",
        "music",
        "shaders",
        "maps"
      )
      val initializer = new GleanyInitializer(pathResolver, settings)
      GleanyGame.runGame(config, new Game(initializer))
    } getOrElse {
      println(parser.usage)
      Gdx.app.exit()
    }



  }
}
