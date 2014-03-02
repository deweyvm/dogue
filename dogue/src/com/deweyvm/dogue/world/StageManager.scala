package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.Implicits
import Implicits._

class StageManager(stages:Pointer[Stage]) {

  def update:StageManager = {
    val inc = Controls.Tab.justPressed.select(1, 0)

    new StageManager(stages.updated(inc).mapOne(_.update))
  }

  def draw() {
    stages.get.draw()
  }
}
