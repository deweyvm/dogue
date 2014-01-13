package com.explatcreations.whatever

import com.explatcreations.gleany.saving.SettingDefaults
import com.explatcreations.gleany.data.Point2i
import com.explatcreations.gleany.graphics.display.Display

object WhateverDefaultSettings extends SettingDefaults {
  override val SfxVolume: Float = 0.05f
  override val MusicVolume: Float = 0.05f
  override val WindowSize: Point2i = new Point2i(Game.Width,Game.Height)
  override val DisplayMode: Int = Display.Windowed.toInt
}
