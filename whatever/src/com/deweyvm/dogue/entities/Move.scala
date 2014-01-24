package com.deweyvm.dogue.entities

import com.badlogic.gdx.net.Socket

trait Move

object Move {
   case object Up extends Move
   case object Down extends Move
   case object Left extends Move
   case object Right extends Move
}
