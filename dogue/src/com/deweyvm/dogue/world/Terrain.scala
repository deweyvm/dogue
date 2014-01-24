package com.deweyvm.dogue.world

class Terrain(cols:Int, rows:Int) {
  val iterations = 4
  val initialDensity = 0.51
  private val cells = (0 until iterations).foldLeft(rand)({case (acc, c) => iterate(acc)})

  def rand:Vector[Vector[Boolean]] = Vector.tabulate(cols,rows) { case (_,_) => scala.math.random > initialDensity}

  def iterate(cells:Vector[Vector[Boolean]]): Vector[Vector[Boolean]] = null

}
