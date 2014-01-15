package com.deweyvm.whatever

import com.deweyvm.gleany.AssetLoader

object Assets {
  import AssetLoader._
  val font = loadFont("RetrovilleNC.ttf", 10)
  val characterMap = loadTexture("Md_curses_16x16")
}
