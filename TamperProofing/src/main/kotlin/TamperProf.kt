package org.example

import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

object TamperProofing {
  private val key = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
  enum class HmacResult { Ok, Expired, Invalid }

  fun hmac(expire: Long, key: String): String {
    val algorithm = "HmacSHA512"
    val secretKey = if (key.isBlank()) TamperProofing.key else key.toByteArray(UTF_8)
    val mac = Mac.getInstance(algorithm).apply {
      init(SecretKeySpec(secretKey, algorithm))
    }

    val msg = "$expire$this"
    val hashBytes = mac.doFinal(msg.toByteArray(UTF_8))
    val result = ByteArray(8 + hashBytes.size)

    hashBytes.copyInto(result, 8)
    expire.toByteArray().copyInto(result, 0)
    return swapInputString(Base64.getEncoder().encodeToString(result))
  }

  fun verify(input: String, expiringHmac: String): HmacResult {
    val bytes = Base64.getDecoder().decode(swapOutputString(expiringHmac))
    val claimedExpiry = bytes.copyOfRange(0, 8).toLong()
    val currentTimestamp = System.currentTimeMillis()
    if (claimedExpiry < currentTimestamp) {
      return HmacResult.Expired
    }
    return if (expiringHmac != hmac(claimedExpiry, input)) HmacResult.Invalid else HmacResult.Ok
  }

  private fun swapInputString(org: String) = swap(org, "+=/", "-_,")
  private fun swapOutputString(org: String) = swap(org, "-_,", "+=/")
}

fun swap(org: String, from: String, to: String): String {
  var result = org
  for (i in from.indices) {
    result = result.replace(from[i], to[i])
  }
  return result
}

fun Long.toByteArray(): ByteArray = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(this).array()
fun ByteArray.toLong(): Long = ByteBuffer.wrap(this).long