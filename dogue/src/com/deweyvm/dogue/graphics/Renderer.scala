package com.deweyvm.dogue.graphics

import com.deweyvm.dogue.entities.Tile


trait Renderer {
  def draw(t:Tile, i:Int, j:Int):Unit
  def render():Unit
}
