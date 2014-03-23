package com.deweyvm.dogue.ui.world

import com.deweyvm.dogue.common.data.Pointer

trait ZoomState
object ZoomState {
  case object Full extends ZoomState
  case object Mini extends ZoomState
  val All = Vector(Mini, Full)
  def getPointer:Pointer[ZoomState] = Pointer.create(All, 0)
}
