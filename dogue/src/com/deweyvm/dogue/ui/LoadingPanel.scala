package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.world.Stage
import com.deweyvm.dogue.common.{logging, CommonImplicits}
import CommonImplicits._
import com.deweyvm.dogue.common.data.control.{Return, Yield, Coroutine}
import com.deweyvm.dogue.input.Controls
import org.scalacheck.Prop.False
import com.deweyvm.dogue.common.logging.Log

case class LoadingPanel(progress:Int,
                        strings:Vector[String],
                        override val rect:Recti,
                        bgColor:Color,
                        panel:DogueFuture[Coroutine[WorldPanel]],
                        makeStage:Panel => Stage,
                        newStage:Option[Stage]=None,
                        failed:Boolean=false) extends Panel(rect, bgColor) {

  private def process[T](c:Coroutine[T], progress:Int, label:String) = {
    //val timeString = "... Done in %dms" format (c.nanos/1000000)
    copy(progress=progress)/*.appendCurrent(timeString).*/prepend(label)
  }

  override def update:Panel = {
    panel.getFailure match {
      case Some(exc) if !failed =>
        val error = logging.Log.formatStackTrace(exc)
        return this.copy(failed=true, strings=error.split(System.lineSeparator()).toVector ++ strings)

      case None => ()
      case _ => return this
    }
    panel.getResult match {
      case Some(c@Return(f)) if Controls.Insert.justPressed =>
        copy(newStage = makeStage(f()).some)
      case Some(c@Yield(p, l, f)) =>
        val future = DogueFuture.createAndRun(f)
        process(c, p, l).copy(panel = future)
      case _ => this
    }


  }

  private def appendCurrent(s:String) = {
    val newStrings = strings match {
      case current +: rest => (current + s) +: rest
      case otherwise => otherwise
    }
    copy(strings = newStrings)
  }

  private def prepend(s:String) = copy(strings = s +: strings)



  override def requestStage:Option[Stage] = newStage

  override def draw() {
    super.draw()
    Text.create(bgColor, Color.White).append("Progress " + progress).draw(10,10)
    strings.zipWithIndex.foreach{case (s, i) =>
      Text.create(bgColor, Color.White).append(s).draw(10, 11 + i)
    }

  }
}
