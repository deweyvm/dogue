package com.deweyvm.whatever.common.net

import java.net.Socket
import com.deweyvm.whatever.common.data.Encoding
import com.deweyvm.whatever.common.Implicits._
//enriched socket for communicating over the whatever server protocol
class EnrichedSocket(sock:Socket) {
  /**
   * requires: same requirements as sock.getInputStream and stream.write
   * does not catch any exceptions
   */
  def send(string:String) {
    sock.getOutputStream.write(Encoding.toBytes(string + "\0"))
  }

  /**
   * does not catch any exceptions
   * @return Some(string) where string has been read from the socket or None if no data was available
   *
   */
  def read():Option[String] = {
    val buff = new Array[Byte](4096) //this cant be shared or it wouldnt be thread safe
    val in = sock.getInputStream
    val available = in.available()
    if (available <= 0) {
      None
    } else {
      val bytesRead = in.read(buff, 0, available)
      val result = Encoding.fromBytes(buff, bytesRead)
      result.some
    }


  }
}
