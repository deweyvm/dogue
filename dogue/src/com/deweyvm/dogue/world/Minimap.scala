package com.deweyvm.dogue.world

class Minimap(world:World, val div:Int) {
  val sampled = world.worldTiles.sample(world.cols/div)
  def draw(iRoot:Int, jRoot:Int) {
    sampled.foreach { case (i, j, t) =>
      t.tile.draw(i + iRoot, j + jRoot)
    }
  }
}
