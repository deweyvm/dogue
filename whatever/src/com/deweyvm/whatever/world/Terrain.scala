package com.deweyvm.whatever.world

class Terrain(cols:Int, rows:Int) {
  val iterations = 4
  private val cells = (0 until iterations).foldLeft(rand)({case (acc, c) => iterate(acc)})

  def rand:Vector[Vector[Boolean]] = Vector.tabulate(cols,rows) { case (a,b) => (scala.math.random*2).toInt}

  def iterate(cells:Vector[Vector[Boolean]]): Vector[Vector[Boolean]] = null

}
