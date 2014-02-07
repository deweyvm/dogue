package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.graphics.Renderer
import com.deweyvm.dogue.Dogue

trait Orientation
case object Vertical extends Orientation
case object Horizontal extends Orientation

class Partition(char:Tile, index:Int, orientation:Orientation) {
   def draw(partitionWidth:Int, partitionHeight:Int) {
     orientation match {
       case Vertical =>
         (0 until partitionHeight) foreach { j =>
           Dogue.renderer.draw(char, index, j)
         }
       case Horizontal =>
         (0 until partitionWidth) foreach { i =>
           Dogue.renderer.draw(char, i, index)
         }

     }
   }
 }
