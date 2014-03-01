package com.deweyvm.dogue.world

import com.deweyvm.dogue.{Game, Assets}


import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls


class StageManager(stages:Pointer[Stage]) {

  def update:StageManager = {
    val inc = if (Controls.Tab.justPressed) 1 else 0

    new StageManager(stages.updated(inc).mapOne(_.update))
  }

  def draw() {
    stages.get.draw()
  }
}
