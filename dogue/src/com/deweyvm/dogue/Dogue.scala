package com.deweyvm.dogue

import com.deweyvm.dogue.graphics.{Renderer, NullRenderer, OglRenderer}
import com.badlogic.gdx.Gdx
import com.deweyvm.dogue.common.Implicits
import Implicits._
import com.deweyvm.dogue.loading.TileSpec


object Dogue {
  private var r:Option[Renderer] = None

  val tileSpec = TileSpec(16, 16, Game.settings.tileSize.get, Game.settings.tileSize.get)

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
  def gdxGraphics = Option(Gdx.graphics)
}
