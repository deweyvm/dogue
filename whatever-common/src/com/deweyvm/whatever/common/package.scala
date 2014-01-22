package com.deweyvm.whatever

import scala.language.implicitConversions
import com.deweyvm.whatever.common.data.{EnrichedFunction2, EnrichedString, EnrichedOption}

package object common {
  object Implicits {
    implicit def any2Option[A](x: A):EnrichedOption[A] = new EnrichedOption(x)
    implicit def string2EnrichedString(x:String):EnrichedString = new EnrichedString(x)
  }
  object Functions {
    implicit def function22EnrichedFunction2[A,B](f:A=>B):EnrichedFunction2[A,B] = new EnrichedFunction2(f)


  }
}
