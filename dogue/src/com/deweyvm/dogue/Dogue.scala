package com.deweyvm.dogue

import com.deweyvm.dogue.graphics.{Renderer, NullRenderer, OglRenderer}
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.common.Implicits
import Implicits._

object Dogue {
  private var r:Option[Renderer] = None

  def behead() {
    r = new NullRenderer().some
  }

  def renderer = {
    if (!r.isDefined) {
      r = new OglRenderer(Assets.marbleDice16x16).some
    }
    r.getOrElse(new NullRenderer)
  }

  def gdxApp = Option(Gdx.app)
  def gdxInput = Option(Gdx.input)
}
