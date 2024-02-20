import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.example.TamperProofing
import java.time.LocalDateTime
import java.time.ZoneOffset

class TamperProfTest: BehaviorSpec({
  Given("TamperProofing") {
    When("generate a valid HMAC") {
      Then("result should be Ok") {
        val expire = LocalDateTime.now().plusMinutes(5).toEpochSecond(ZoneOffset.UTC)
        val origin = "origin"
        val code = TamperProofing.hmac(expire, origin)
        val result = TamperProofing.verify(code, origin)
        result shouldBe TamperProofing.HmacResult.Ok
      }
    }
    When("generate an expired HMAC") {
      Then("result should be Expired") {
        val expire = LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.UTC)
        val origin = "origin"
        val code = TamperProofing.hmac(expire, origin)
        val result = TamperProofing.verify(code, origin)
        result shouldBe TamperProofing.HmacResult.Expired
      }
    }
    When("generate an invalid HMAC") {
      Then("result should be Invalid") {
        val expire = LocalDateTime.now().plusMinutes(5).toEpochSecond(ZoneOffset.UTC)
        val code = TamperProofing.hmac(expire, "origin")
        val result = TamperProofing.verify(code, "another")
        result shouldBe TamperProofing.HmacResult.Invalid
      }
    }
  }
})
