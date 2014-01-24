package com.deweyvm.dogue.entities

import com.deweyvm.dogue.input.Controls


class Player {
  private var move:Option[Move] = None
  def getMove : Option[Move] = move
  def update() {
    if (Controls.Up.justPressed) {
      move = Some(Move.Up)
    }
  }

  def draw() {

  }
}
