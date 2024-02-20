package org.example

import java.time.LocalDateTime
import java.time.ZoneOffset

fun main() {
  val expireDate = LocalDateTime.now()
    .plusMinutes(5)
  val expire = expireDate.toEpochSecond(ZoneOffset.UTC)
  println(expire)
  val origin = "간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?간단한 페이지라서 내부적으로 제작해도될 것 같은데, 이정도의 간단한페이지도 BX작업을 요청드려야하나요?"
  val code = TamperProofing.hmac(expire, origin)
  println(code)
  println(TamperProofing.verify(code, origin))
}
