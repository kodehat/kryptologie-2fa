package de.codehat.tfademo

import org.junit.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@RunWith(JUnitParamsRunner::class)
open class TimeBasedOneTimePasswordGeneratorTest: HmacOneTimePasswordGeneratorTest() {

    @Test
    @Parameters(
        "HmacSHA1,   59,          94287082",
        "HmacSHA1,   1111111109,   7081804",
        "HmacSHA1,   1111111111,  14050471",
        "HmacSHA1,   1234567890,  89005924",
        "HmacSHA1,   2000000000,  69279037",
        "HmacSHA1,   20000000000, 65353130",
        "HmacSHA256, 59,          46119246",
        "HmacSHA256, 1111111109,  68084774",
        "HmacSHA256, 1111111111,  67062674",
        "HmacSHA256, 1234567890,  91819424",
        "HmacSHA256, 2000000000,  90698825",
        "HmacSHA256, 20000000000, 77737706",
        "HmacSHA512, 59,          90693936",
        "HmacSHA512, 1111111109,  25091201",
        "HmacSHA512, 1111111111,  99943326",
        "HmacSHA512, 1234567890,  93441116",
        "HmacSHA512, 2000000000,  38618901",
        "HmacSHA512, 20000000000, 47863826"
    )
    @Throws(Exception::class)
    fun testGenerateOneTimePassword(algorithm: String, epochSeconds: Long, expectedOneTimePassword: Int) {

        val totp = TimeBasedOneTimePasswordGenerator(30, TimeUnit.SECONDS, 8, algorithm)

        val date = Date(TimeUnit.SECONDS.toMillis(epochSeconds))

        assertEquals(expectedOneTimePassword, totp.generateOneTimePassword(getSecretKeyForAlgorithm(algorithm), date).result)
    }

    private fun getSecretKeyForAlgorithm(algorithm: String): SecretKey {
        val keyString: String

        when (algorithm) {
            "HmacSHA1" -> {
                keyString = "12345678901234567890"
            }

            "HmacSHA256" -> {
                keyString = "12345678901234567890123456789012"
            }

            "HmacSHA512" -> {
                keyString = "1234567890123456789012345678901234567890123456789012345678901234"
            }

            else -> {
                throw IllegalArgumentException("Unexpected algorithm: $algorithm")
            }
        }

        return SecretKeySpec(keyString.toByteArray(StandardCharsets.US_ASCII), "RAW")
    }
}