package com.deweyvm.dogue.graphics

import com.deweyvm.dogue.entities.Tile

object WindowRenderer {
  def create = WindowRenderer(Map(), 0, 0)
}

case class WindowRenderer(draws:Map[(Int,Int), Tile], originX:Int, originY:Int) {
  def at(i:Int, j:Int) = copy(originX = i, originY = j)
  def move(i:Int, j:Int) = copy(originX = originX + i, originY = originY + j)
  def <+(i:Int, j:Int, tile:Tile) = {
    val updated = draws.updated((i + originX, j + originY), tile)
    copy(draws = updated)
  }

  def <+~(t:(Int,Int,Tile)) = this.<+ _ tupled t
  def <+?(t:Option[(Int,Int,Tile)]) = t.map {this <+~ _}.getOrElse(this)
  def <++(draws:Seq[(Int,Int,Tile)]) = {
    draws.foldLeft(this) { _ <+~ _}
  }

  /**
   * This operation is not commutative
   */
  def <+|(f:WindowRenderer => WindowRenderer) = {
    val drawn = f(WindowRenderer(Map(), originX, originY))
    WindowRenderer(draws ++ drawn.draws, drawn.originX, drawn.originY)
  }

  def <+|?(f:Option[WindowRenderer => WindowRenderer]) = {
    f.map {this <+| _}.getOrElse(this)
  }

  def <++|(fs:Seq[WindowRenderer => WindowRenderer]) = {
    fs.foldLeft(this) { _ <+| _}
  }
}
