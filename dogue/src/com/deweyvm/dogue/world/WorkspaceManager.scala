package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer

class WorkspaceManager(stages:Pointer[Workspace]) {

  def update:WorkspaceManager = {
    val inc = Controls.Tab.justPressed.select(1, 0)

    new WorkspaceManager(stages.updated(inc).getMap(_.update))
  }

  def draw(r:WindowRenderer):WindowRenderer = {
    stages.get.draw(r)
  }
}
