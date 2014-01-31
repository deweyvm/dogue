package com.deweyvm.dogue.net


trait Transmitter[T] {
  def enqueue(s:T):Unit
  def dequeue:Vector[T]
}
