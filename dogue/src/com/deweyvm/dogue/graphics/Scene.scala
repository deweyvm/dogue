package com.deweyvm.dogue.graphics

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.common.Implicits
import Implicits._

class Scene(cols:Int, rows:Int) {
  val array = Array.tabulate[Option[Tile]](cols*rows) {_ => None}
  def set(i:Int, j:Int, t:Tile) {
    val k = Array2d.coordsToIndex(i, j, cols)
    array(k) = t.some
  }

  def foreach(f:(Int, Int, Tile) => Unit) {
    for (k <- 0 until rows*cols) {
      val (i, j) = Array2d.indexToCoords(k, cols)
      val tile = array(k)
      tile foreach {f(i, j, _)}
    }
  }

}
