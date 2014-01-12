package com.explatcreations.whatever

import com.explatcreations.gleany.{GleanyInitializer, GleanyConfig, GleanyGame}
import com.explatcreations.gleany.logging.Logger
import com.explatcreations.gleany.files.PathResolver
import com.explatcreations.gleany.saving.{Settings, SettingDefaults, ControlNameCollection, ControlName}
import com.explatcreations.gleany.graphics.display.Display
import com.explatcreations.gleany.data.Point2i

class WhateverControl(descriptor: String) extends ControlName {
  override def name: String = descriptor
}

object WhateverControls extends ControlNameCollection[WhateverControl] {
  def fromString(string: String): Option[com.explatcreations.whatever.WhateverControl] = None
  def makeJoypadDefault: Map[String,String] = Map()
  def makeKeyboardDefault: Map[String,java.lang.Float] = Map()
  def values: Seq[com.explatcreations.whatever.WhateverControl] = Seq()
}

object WhateverDefaultSettings extends SettingDefaults {
  override val SfxVolume: Float = 0.05f
  override val MusicVolume: Float = 0.05f
  override val WindowSize: Point2i = new Point2i(600,600)
  override val DisplayMode: Int = Display.Windowed.toInt
}


object Main {
  def main(args: Array[String]) {
    handleArgs(args)
    //val settings = new Settings(MissionControls, MissionDefaultSettings)
    val iconPath = "sprites/icon.gif"
    val settings = new Settings(WhateverControls, WhateverDefaultSettings)
    val config = new GleanyConfig(settings, "Mission", Some(iconPath))
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
    if (showVersion) {
      println("unknown")
      sys.exit(0)
    }
    Logger.attachCrasher(isDebug)
  }
}
