package com.deweyvm.dogue.ui

trait Menu[T] {
  def update:Menu[T]
  def getResult:Option[T]
  def draw():Unit
}
