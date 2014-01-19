package com.deweyvm.whatever.server

object ThreadManager {
  def spawn(task:Task) {
    new Thread(new Runnable {
      override def run() {
        task.execute()
      }
    }).start()
  }
}
