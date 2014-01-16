package com.deweyvm.whatever.entities

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.whatever.graphics.GlyphFactory

//code page 437
//uppercase special characters are suffixed with _u due to the possible error
// "Code$Ä$ differs only in case from Code$ä$. Such classes will overwrite one
// another on case-insensitive filesystems."

object Code {

  val ☺ = Code(1)
  val ☻ = Code(2)
  val ♥ = Code(3)
  val ♦ = Code(4)
  val ♣ = Code(5)
  val ♠ = Code(6)
  val ● = Code(7)
  val ◘ = Code(8)
  val ◦ = Code(9)
  val ◙ = Code(10)
  val ♂ = Code(11)
  val ♀ = Code(12)
  val ♪ = Code(13)
  val ♫ = Code(14)
  val ☼ = Code(15)
  val ► = Code(16)
  val ◄ = Code(17)
  val ↕ = Code(18)
  val `‼` = Code(19)
  val ¶ = Code(20)
  val § = Code(21)
  val `‗` = Code(22)
  val ↨ = Code(23)
  val ↑ = Code(24)
  val ↓ = Code(25)
  val → = Code(26)
  val `←` = Code(27)
  val ∟ = Code(28)
  val ↔ = Code(29)
  val ▲ = Code(30)
  val ▼ = Code(31)
  val ` ` = Code(32)
  val ! = Code(33)
  val `"` = Code(34)
  val `#` = Code(35)
  val $ = Code(36)
  val % = Code(37)
  val & = Code(38)
  val `'` = Code(39)
  val `(` = Code(40)
  val `)` = Code(41)
  val * = Code(42)
  val + = Code(43)
  val `,` = Code(44)
  val - = Code(45)
  val `.` = Code(46)
  val / = Code(47)
  val `0` = Code(48)
  val `1` = Code(49)
  val `2` = Code(50)
  val `3` = Code(51)
  val `4` = Code(52)
  val `5` = Code(53)
  val `6` = Code(54)
  val `7` = Code(55)
  val `8` = Code(56)
  val `9` = Code(57)
  val `:` = Code(58)
  val `;` = Code(59)
  val < = Code(60)
  val `=` = Code(61)
  val > = Code(62)
  val ? = Code(63)
  val `@` = Code(64)
  val A = Code(65)
  val B = Code(66)
  val C = Code(67)
  val D = Code(68)
  val E = Code(69)
  val F = Code(70)
  val G = Code(71)
  val H = Code(72)
  val I = Code(73)
  val J = Code(74)
  val K = Code(75)
  val L = Code(76)
  val M = Code(77)
  val N = Code(78)
  val O = Code(79)
  val P = Code(80)
  val Q = Code(81)
  val R = Code(82)
  val S = Code(83)
  val T = Code(84)
  val U = Code(85)
  val V = Code(86)
  val W = Code(87)
  val X = Code(88)
  val Y = Code(89)
  val Z = Code(90)
  val `[` = Code(91)
  val \ = Code(92)
  val `]` = Code(93)
  val ^ = Code(94)
  val underscore = Code(95)
  val grave = Code(96)
  val a = Code(97)
  val b = Code(98)
  val c = Code(99)
  val d = Code(100)
  val e = Code(101)
  val f = Code(102)
  val g = Code(103)
  val h = Code(104)
  val i = Code(105)
  val j = Code(106)
  val k = Code(107)
  val l = Code(108)
  val m = Code(109)
  val n = Code(110)
  val o = Code(111)
  val p = Code(112)
  val q = Code(113)
  val r = Code(114)
  val s = Code(115)
  val t = Code(116)
  val u = Code(117)
  val v = Code(118)
  val w = Code(119)
  val x = Code(120)
  val y = Code(121)
  val z = Code(122)
  val `{` = Code(123)
  val | = Code(124)
  val `}` = Code(125)
  val ~ = Code(126)
  val ⌂ = Code(127)
  val Ç_u = Code(128)
  val ü = Code(129)
  val é = Code(130)
  val â = Code(131)
  val ä = Code(132)
  val à = Code(133)
  val å = Code(134)
  val ç = Code(135)
  val ê = Code(136)
  val ë = Code(137)
  val è = Code(138)
  val ï = Code(139)
  val î = Code(140)
  val ì = Code(141)
  val Ä_u = Code(142)
  val Å_u = Code(143)
  val É_u = Code(144)
  val æ = Code(145)
  val Æ_u = Code(146)
  val ô = Code(147)
  val ö = Code(148)
  val ò = Code(149)
  val û = Code(150)
  val ù = Code(151)
  val ÿ = Code(152)
  val Ö_u = Code(153)
  val Ü_u = Code(154)
  val `¢` = Code(155)
  val `£` = Code(156)
  val `¥` = Code(157)
  val `₧` = Code(158)
  val ƒ = Code(159)
  val á = Code(160)
  val í = Code(161)
  val ó = Code(162)
  val ú = Code(163)
  val ñ = Code(164)
  val Ñ_u = Code(165)
  val ª = Code(166)
  val º = Code(167)
  val `¿` = Code(168)
  val ⌐ = Code(169)
  val ¬ = Code(170)
  val `½` = Code(171)
  val `¼` = Code(172)
  val `¡` = Code(173)
  val `«` = Code(174)
  val `»` = Code(175)
  val ░ = Code(176)
  val ▒ = Code(177)
  val ▓ = Code(178)
  val │ = Code(179)
  val ┤ = Code(180)
  val ╡ = Code(181)
  val ╢ = Code(182)
  val ╖ = Code(183)
  val ╕ = Code(184)
  val ╣ = Code(185)
  val ║ = Code(186)
  val ╗ = Code(187)
  val ╝ = Code(188)
  val ╜ = Code(189)
  val ╛ = Code(190)
  val ┐ = Code(191)
  val └ = Code(192)
  val ┴ = Code(193)
  val ┬ = Code(194)
  val ├ = Code(195)
  val ─ = Code(196)
  val ┼ = Code(197)
  val ╞ = Code(198)
  val ╟ = Code(199)
  val ╚ = Code(200)
  val ╔ = Code(201)
  val ╩ = Code(202)
  val ╦ = Code(203)
  val ╠ = Code(204)
  val ═ = Code(205)
  val ╬ = Code(206)
  val ╧ = Code(207)
  val ╨ = Code(208)
  val ╤ = Code(209)
  val ╥ = Code(210)
  val ╙ = Code(211)
  val ╘ = Code(212)
  val ╒ = Code(213)
  val ╓ = Code(214)
  val ╫ = Code(215)
  val ╪ = Code(216)
  val ┘ = Code(217)
  val ┌ = Code(218)
  val █ = Code(219)
  val ▄ = Code(220)
  val ▌ = Code(221)
  val ▐ = Code(222)
  val ▀ = Code(223)
  val α = Code(224)
  val β = Code(225)
  val Γ = Code(226)
  val π = Code(227)
  val Σ_u = Code(228)
  val σ = Code(229)
  val μ = Code(230)
  val τ = Code(231)
  val Φ = Code(232)
  val Θ = Code(233)
  val Ω = Code(234)
  val δ = Code(235)
  val ∞ = Code(236)
  val φ = Code(237)
  val ϵ = Code(238)
  val ∩ = Code(239)
  val ≡ = Code(240)
  val ± = Code(241)
  val ≥ = Code(242)
  val ≤ = Code(243)
  val ⌠ = Code(244)
  val ⌡ = Code(245)
  val ÷ = Code(246)
  val ≈ = Code(247)
  val ° = Code(248)
  val ▪ = Code(249)
  val `·` = Code(250)
  val √ = Code(251)
  val ⁿ = Code(252)
  val `²` = Code(253)
  val ■ = Code(254)
  val All = (0 until 256) map {c => Code(c)}
}

case class Code(index:Int) {
  def makeTile(bgColor:Color, fgColor:Color, factory:GlyphFactory) = {
    new Tile(bgColor, fgColor, index, factory)
  }
}