package com.deweyvm.whatever

import com.deweyvm.gleany.AssetLoader
import com.deweyvm.whatever.graphics.GlyphFactory

object Assets {
  import AssetLoader._
  val font = loadFont("RetrovilleNC.ttf", 10)
  val marbleDice16x16 = loadTexture("Md_curses_16x16")
  val page437_16x16 = new GlyphFactory(16, 16, 16, 16, marbleDice16x16)
}
