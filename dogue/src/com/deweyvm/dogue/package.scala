package com.deweyvm

import com.deweyvm.dogue.world.{AltitudinalRegion, LatitudinalRegion}
import com.deweyvm.dogue.common.Implicits
import Implicits._
package object dogue {
  object DogueImplicits {
    implicit val latitudeOrdering = new Ordering[LatitudinalRegion] {
      def compare(m1:LatitudinalRegion, m2:LatitudinalRegion) =
        m1.range.max.compare(m2.range.max)

    }

    implicit val altitudeOrdering = new Ordering[AltitudinalRegion] {
      def compare(m1:AltitudinalRegion, m2:AltitudinalRegion) =
        m1.range.max.d.compare(m2.range.max.d)

    }

  }
}
