package com.deweyvm.whatever.server

import java.net.Socket
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.data.Encoding

object Reader {
  //todo -- write tests for this method
  def split(s:String, t:Char):Vector[String] = {
    val (lines, last) = s.foldLeft(Vector[String](), "") { case ((lines,  l), c) =>
      if (c == t) {
        (lines ++ Vector(l), "")
      } else {
        (lines, l + c)
      }
    }
    lines ++ Vector(last)
  }
  def test() {
    val tests = Vector(
      ("this|is|a|test", '|', 4),
      ("", '|', 1),
      ("|", '|', 2),
      ("abc|", '|', 2)
    )

    tests foreach { case (line, sep, count) =>
      val sp = split(line, sep)
      printf("line to split: <%s>\n", line)
      for (s <- sp) {
        printf("    <%s>\n", s)
      }
      printf("%d ==? %d\n\n", sp.length, count)
      assert(sp.length == count)
    }
  }
}

class Reader(socket:Socket, parent:Server) extends Runnable {
  import Reader._
  var running = true
  val out = socket.getOutputStream
  val in = socket.getInputStream
  val inBuffer = ArrayBuffer[String]()
  var current = ""
  val buff = new Array[Byte](4096)

  def close() {
    println("Shutting down")
    socket.close()
    running = false
    parent.running = false
  }


  override def run() {
    while(running && socket.isConnected) {
      val available = in.available()
      if (available > 0) {
        val bytesRead = in.read(buff, 0, available)
        if (bytesRead > 0) {
          val next = Encoding.fromBytes(buff, bytesRead)
          val lines = split(next, '\0')
          val last = lines(lines.length - 1)
          val first = lines.dropRight(1)
          for (s <- first) {
            current += s
            inBuffer += current
            current = ""
          }
          current = last

          for (s <- inBuffer) {
            new Thread(new Worker(s, close)).start()
          }
          inBuffer.clear()
        }
      }
    }
  }
}


