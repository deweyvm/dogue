package com.deweyvm.whatever

import com.deweyvm.gleany.{GleanyInitializer, GleanyConfig, GleanyGame}
import com.deweyvm.gleany.logging.Logger
import com.deweyvm.gleany.files.PathResolver
import com.deweyvm.gleany.saving.Settings
import com.deweyvm.whatever.input.WhateverControls
import com.deweyvm.whatever.loading.WhateverDefaultSettings


object Main {
  def main(args: Array[String]) {
    handleArgs(args)
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
  }

  def handleArgs(args: Seq[String]) {
    val isDebug = args.contains("--debug")
    val showVersion = args.contains("--version")
    val remote = args.contains("--remote")
    Game.globals.IsDebugMode = isDebug
    Game.globals.IsRemote = remote
    if (showVersion) {
      println("unknown")
      sys.exit(0)
    }
    Logger.attachCrasher(isDebug)
  }
}
