package com.deweyvm.dogue.loading

import com.deweyvm.gleany.saving.SettingDefaults
import com.deweyvm.gleany.data.Point2i
import com.deweyvm.gleany.graphics.display.Display
import com.deweyvm.dogue.Game

object WhateverDefaultSettings extends SettingDefaults {
  override val SfxVolume: Float = 0.05f
  override val MusicVolume: Float = 0.05f
  override val WindowSize: Point2i = new Point2i(Game.Width,Game.Height)
  override val DisplayMode: Int = Display.Windowed.toInt
}
