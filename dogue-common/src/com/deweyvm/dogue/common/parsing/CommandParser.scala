package com.deweyvm.dogue.common.parsing

import scala.util.parsing.combinator.RegexParsers
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.dogue.common.protocol._
import com.deweyvm.dogue.common.protocol.Invalid


object CommandParser {
  def test() {
    val parser = new CommandParser
    val tests:List[(String, Option[Int])] = List(
      ("say a b \"preserve    whitespace\"", 1.some),
      ("pong from to a b c", 3.some),
      ("pong from                  \tto a b c", 3.some),
      ("ping from to a", 1.some),
      ("say a", None),
      (" a b c d", None),
      ("say a b", 0.some),
      ("say 6bdaeba28f26b3e3 6bdaeba28f26b3e3 ?", 1.some),
      ("says 6bdaeba28f26b3e3 6bdaeba28f26b3e3 ?", None),
      ("say 5e01405ec801cfa4 5e01405ec801cfa4 HUH?", 1.some),
      ("greet flare &unknown& identify", 1.some),
      ("say a b \"this is a test of parsing string literals\"", 1.some)

    )



    def parse(s:String) = parser.parseAll(parser.parseCommand, s)

    tests foreach { case (s, expected) =>
      try {
        val parsed = parse(s)
        val index = (tests map {_._1}).indexOf(s)
        assert (parsed.successful == expected.isDefined, index + " " + s + "\n" + parsed)
        parser.getCommand(s).toOption foreach { p =>
          expected foreach { i =>
            assert(p.args.length == i, "%d != %d" format (p.args.length, i))
          }
          //assert(p.toString == s.toString, "\"%s\" != \"%s\"" format (p.toString, s.toString))

        }
      } catch {
        case p:ParseError =>
          assert(expected.isEmpty, p.getMessage)
      }
    }
  }
}
class ParseError(msg:String) extends Exception(msg:String)
class CommandParser extends RegexParsers {
  override type Elem = Char

  def parseOp = opChoices<~"""(?!\w)""".r
  def opChoices = sayOp | pingOp | pongOp | greetOp | quitOp
  def sayOp   = "say".r   ^^ { _ => DogueOp.Say }
  def pingOp  = "ping".r  ^^ { _ => DogueOp.Ping }
  def pongOp  = "pong".r  ^^ { _ => DogueOp.Pong }
  def greetOp = "greet".r ^^ { _ => DogueOp.Greet }
  def quitOp  = "quit".r  ^^ { _ => DogueOp.Quit }
  def parseArg = """[^\s\x{0}"]+""".r
  def parseWord = parseString | parseArg //"""\w+""".r
  def parseString = "\"".r~>"""[^"]*""".r<~"\"".r
  def parseArgs = rep1(parseWord)
  def parseCommand: Parser[Command] = parseWord~parseWord~parseWord~parseArgs.? ^^ {  case rawOp~src~dest~args =>
      Command(getOp(rawOp), src, dest, args map {_.toVector} getOrElse Vector())

  }

  def getCommand(input:String):DogueMessage = {
    val parseResult = parse(parseCommand, input)
    if (parseResult.successful) {
      parseResult.get
    } else {
      Invalid(input, parseResult.toString)
    }
  }

  def getOp(input:String):DogueOp = {
    val parseResult = parse(parseOp, input)
    if (parseResult.successful) {
      parseResult.get
    } else {
      throw new ParseError(parseResult.toString)
    }

  }
}
