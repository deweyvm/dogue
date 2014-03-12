package com.deweyvm.dogue.world

import com.deweyvm.dogue.entities.Tile

trait Orientation
case object Vertical extends Orientation
case object Horizontal extends Orientation

class Partition(char:Tile, index:Int, orientation:Orientation) {
   def draw(partitionWidth:Int, partitionHeight:Int) {
     orientation match {
       case Vertical =>
         (0 until partitionHeight) foreach { j =>
           char.draw(index, j)
         }
       case Horizontal =>
         (0 until partitionWidth) foreach { i =>
           char.draw(i, index)
         }

     }
   }
 }
