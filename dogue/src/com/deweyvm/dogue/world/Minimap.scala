package com.deweyvm.dogue.world

class Minimap(world:World, div:Int) {
  val sampled = world.tiles.sample(world.cols/div)
  def draw(iRoot:Int, jRoot:Int) {
    sampled.foreach { case (i, j, t) =>
      t.tile.draw(i + iRoot, j + jRoot)
    }
  }
}
