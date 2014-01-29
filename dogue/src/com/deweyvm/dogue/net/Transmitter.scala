package com.deweyvm.dogue.net


trait Transmitter {
  def enqueue(s:String):Unit
  def dequeue:Vector[String]
}
