package com.deweyvm.whatever.server

abstract class Task {
  def execute():Unit
  def run() {
    ThreadManager.spawn(this)
  }
}
