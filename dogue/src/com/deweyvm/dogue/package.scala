package com.deweyvm

import com.deweyvm.dogue.world.LatitudinalRegion
import com.deweyvm.dogue.world.AltitudinalRegion
import com.deweyvm.dogue.common.CommonImplicits

package object dogue {
  object DogueImplicits {
    implicit class Meters(val d:Double) extends AnyVal {
      def m:Meters = this
      def f = d.toFloat
      def >(other:Meters) = d > other.d
      def >=(other:Meters) = d >= other.d
      def <(other:Meters) = d < other.d
      def <=(other:Meters) = d <= other.d
      def +(other:Meters) = (d + other.d).m
      def unary_- = Meters(-d)
      override def toString = "%.2fm" format d
    }

    implicit class Pressure(val d:Double) extends AnyVal {
      def atm:Pressure = this
    }

    implicit class Celcius(val d:Double) extends AnyVal {
      def C:Celcius = this
    }

    /**
     * rainfall has units of mm/year
     * @param d
     */
    implicit class Rainfall(val d:Double) extends AnyVal {
      def `mm/yr`:Rainfall = this
    }

    implicit val metersOrdered = new Ordering[Meters] {
      def compare(m1:Meters, m2:Meters) = m1.d.compare(m2.d)
    }

    implicit val celciusOrdered = new Ordering[Celcius] {
      def compare(c1:Celcius, c2:Celcius) = c1.d.compare(c2.d)
    }

    implicit val rainfallOrdered = new Ordering[Rainfall] {
      def compare(r1:Rainfall, r2:Rainfall) = r1.d.compare(r2.d)
    }


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
