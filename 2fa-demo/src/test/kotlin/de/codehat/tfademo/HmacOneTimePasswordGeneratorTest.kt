package de.codehat.tfademo

import org.junit.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@RunWith(JUnitParamsRunner::class)
open class HmacOneTimePasswordGeneratorTest {
    /**
     * Tests generation of one-time passwords using the test vectors from
     * [RFC&nbsp;4226, Appendix D](https://tools.ietf.org/html/rfc4226#appendix-D).
     */
    @Test
    @Parameters(
        "0, 755224",
        "1, 287082",
        "2, 359152",
        "3, 969429",
        "4, 338314",
        "5, 254676",
        "6, 287922",
        "7, 162583",
        "8, 399871",
        "9, 520489"
    )
    @Throws(Exception::class)
    fun testGenerateOneTimePassword(counter: Int, expectedOneTimePassword: Int) {
        val hmacOneTimePasswordGenerator = this.getDefaultGenerator()

        val key = SecretKeySpec("12345678901234567890".toByteArray(StandardCharsets.US_ASCII), "RAW")
        assertEquals(
            expectedOneTimePassword,
            hmacOneTimePasswordGenerator.generateOneTimePassword(key, counter.toLong()).result
        )
    }

    private fun getDefaultGenerator(): HmacOneTimePasswordGenerator {
        return HmacOneTimePasswordGenerator()
    }

}