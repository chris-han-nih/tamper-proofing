package org.example

import java.time.LocalDateTime
import java.time.ZoneOffset

fun main() {
  val expireDate = LocalDateTime.now()
    .plusMinutes(5)
  val expire = expireDate.toEpochSecond(ZoneOffset.UTC)
  val code = TamperProofing.hmac(expire, "davehan")
  println(code)
  println(TamperProofing.verify("davehan", code))
}
