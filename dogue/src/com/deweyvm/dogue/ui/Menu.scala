package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.WindowRenderer

trait Menu[T] {
  def update:Menu[T]
  def getResult:Option[T]
  def draw(r:WindowRenderer):WindowRenderer
}
