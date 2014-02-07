package com.deweyvm.dogue.input

import com.deweyvm.gleany.input.Control

class NullControl extends Control[Boolean] {
  override def update() {  }
  override def isPressed: Boolean = false
  override def justPressed: Boolean = false
  override def justReleased: Boolean = false
  override def zip(start: Int, num: Int): Boolean = false
}
