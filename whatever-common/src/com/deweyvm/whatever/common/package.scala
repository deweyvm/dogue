package com.deweyvm.whatever

import java.net.Socket
import scala.language.implicitConversions
import com.deweyvm.whatever.common.data.{EnrichedFunction2, EnrichedString, EnrichedOption}
import com.deweyvm.whatever.common.io.{EnrichedInputStream, EnrichedOutputStream, EnrichedSocket}
import java.io.{OutputStream, InputStream}

package object common {
  object Implicits {
    implicit def any2Option[A](x: A):EnrichedOption[A] = new EnrichedOption(x)
    implicit def string2EnrichedString(x:String):EnrichedString = new EnrichedString(x)
    implicit def socket2EnrichedSocket(sock:Socket):EnrichedSocket = new EnrichedSocket(sock)
    implicit def outputStream2EnrichedOutputStream(out:OutputStream):EnrichedOutputStream =
      new EnrichedOutputStream(out)
    implicit def inputStream2EnrichedInputStream(in:InputStream):EnrichedInputStream =
      new EnrichedInputStream(in)
  }

  object Functions {
    implicit def function22EnrichedFunction2[A,B](f:A=>B):EnrichedFunction2[A,B] = new EnrichedFunction2(f)


  }
}
