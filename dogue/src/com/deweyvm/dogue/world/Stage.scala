package com.deweyvm.dogue.world

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui._
import com.deweyvm.dogue.entities.Tile

import com.deweyvm.dogue.net.Client
import com.deweyvm.dogue.common.data.{Code, Array2d}
import com.deweyvm.gleany.data.{Point2d, Recti}
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

trait StageType

object Stage {
  case object Blank extends StageType
  case object Title extends StageType
  case object Chat extends StageType
  case object World extends StageType
}

object Workspace {
  def create(screenCols:Int, screenRows:Int, windows:Vector[Window]) = {
    new Workspace(screenCols, screenRows, windows, new WindowManager(screenCols, screenRows))
  }
}

case class Workspace private(screenCols:Int, screenRows:Int, windows:Vector[Window], manager:WindowManager) {
  def update:Workspace = copy(windows = manager.updateWorkspace(windows))
  def draw() {
    manager.draw(windows)
  }
}
/*case class Stage(cols:Int, rows:Int, serverStatus:Text) {
  val manager = new WindowManager
  val rect = Recti(0, 0, cols, rows)


  def update:Stage = {
    //update all windows, get output of all windows, give output to all windows as input and process

    this.copy(windows = win,
              serverStatus = serverStatus.setString(Client.instance.getStatus))
  }

  def draw() {
    windows foreach { _.draw() }
    manager.draw(windows)
    serverStatus.draw(cols - serverStatus.width, rows - 1)
  }

  def isSolid(i:Int, j:Int): Boolean = {
    !rect.contains(Point2d(i,j)) || (windows exists { p =>
      p.contains(i, j)
    })
  }

}*/


