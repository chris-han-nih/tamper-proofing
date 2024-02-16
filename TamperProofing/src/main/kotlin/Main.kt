package org.example

fun main() {
  val code = TamperProofing.Hmac(17080750119000, "davehan")
  println(code)
  println(System.currentTimeMillis())
  println(TamperProofing.verify("davehan", code))
}
//1708082561051
//17080750119000