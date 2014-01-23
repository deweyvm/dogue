package com.deweyvm.whatever.common.io

import java.io.OutputStream
import com.deweyvm.whatever.common.data.Encoding

class EnrichedOutputStream(out:OutputStream) {
  def transmit(string:String) {
    out.write(Encoding.toBytes(string + "\0"))
  }
}
