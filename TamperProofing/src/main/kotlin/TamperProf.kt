package org.example

import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

object TamperProofing {
  private val defaultKey = "christmas".toByteArray(UTF_8)
  private const val ALGORITHM = "HmacSHA512"

  enum class HmacResult { Ok, Expired, Invalid }

  fun hmac(expire: Long, origin: String, key: String = ""): String {
    val secretKey = key.takeIf { it.isNotBlank() }?.toByteArray(UTF_8) ?: defaultKey
    val hashBytes = createHmac(secretKey, "$expire$origin")
    val result = ByteBuffer.allocate(Long.SIZE_BYTES + hashBytes.size).putLong(expire).put(hashBytes).array()
    return Base64.getEncoder().encodeToString(result).swap("+=/", "-_,")
  }

  fun verify(expiringHmac: String, origin: String, key: String = ""): HmacResult {
    val bytes = Base64.getDecoder().decode(expiringHmac.swap("-_,", "+=/"))
    val byteBuffer = ByteBuffer.wrap(bytes)
    val claimedExpiry = byteBuffer.long

    val currentMillis = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    if (claimedExpiry < currentMillis) return HmacResult.Expired
    if (expiringHmac != hmac(claimedExpiry, origin, key)) return HmacResult.Invalid
    return HmacResult.Ok
  }

  private fun createHmac(key: ByteArray, data: String): ByteArray =
    Mac.getInstance(ALGORITHM).apply {
      init(SecretKeySpec(key, algorithm))
    }.doFinal(data.toByteArray(UTF_8))

  private fun String.swap(from: String, to: String): String =
    map { char ->
      val index = from.indexOf(char)
      if (index >= 0) to[index] else char
    }.joinToString("")
}
