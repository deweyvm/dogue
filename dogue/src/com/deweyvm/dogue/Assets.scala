package com.deweyvm.dogue

import com.deweyvm.gleany.AssetLoader
import com.deweyvm.dogue.graphics.Tileset

object Assets {
  import AssetLoader._
  lazy val marbleDice16x16 = new Tileset(16, 16, Dogue.tileSpec.width, Dogue.tileSpec.height, loadTexture("Md_curses_16x16"))
}
