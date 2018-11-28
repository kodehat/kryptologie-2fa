package de.codehat.tfademo

import java.nio.ByteBuffer
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.SecretKey
import kotlin.experimental.and

data class OneTimePasswordData(
    val counter: Long,
    val bufferEmpty: String,
    val bufferWithCounter: String,
    val hmac: String,
    val offset: Byte,
    val bufferSteps: List<String>,
    val hotp: Int,
    val result: Int
)

open class HmacOneTimePasswordGenerator(private val passwordLength: Int,
                                        private val algorithm: String) {

    companion object {
        const val DEFAULT_PASSWORD_LENGTH = 6
        const val DEFAULT_HMAC_ALGORITHM = "HmacSHA1"
    }

    private val modDivisor: Int

    constructor(passwordLength: Int = DEFAULT_PASSWORD_LENGTH) : this(passwordLength, DEFAULT_HMAC_ALGORITHM)

    init {
        when (passwordLength) {
            6 -> {
                this.modDivisor = 1000000
            }

            7 -> {
                this.modDivisor = 10000000
            }

            8 -> {
                this.modDivisor = 100000000
            }

            else -> {
                throw IllegalArgumentException("Password length must be between 6 and 8 digits.")
            }
        }

        Mac.getInstance(algorithm)
    }

    fun generateOneTimePassword(secretKey: SecretKey, counter: Long): OneTimePasswordData {
        val mac: Mac

        try {
            mac = Mac.getInstance(this.algorithm)
            mac.init(secretKey)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }

        val buffer = ByteBuffer.allocate(8)
        val emptyBufferStr = Arrays.toString(buffer.array())
        buffer.putLong(0, counter)
        val bufferWithCounterStr = Arrays.toString(buffer.array())

        val hmac = mac.doFinal(buffer.array())
        val hmacStr = Arrays.toString(hmac)
        val offset = hmac[hmac.size - 1] and 0x0f

        val bufferStepsStr = mutableListOf<String>()
        for (i in 0..3) {
            buffer.put(i, hmac[i + offset])
            bufferStepsStr.add(i, hmac[i + offset].toString())
        }

        val firstFourBytes = buffer.getInt(0)
        val hotp = buffer.getInt(0) and 0x7fffffff
        //println("Alternative: ${(buffer.getInt(0) % Math.pow(2.0, 31.0)).toInt()}")

        val res = hotp % this.modDivisor

        return OneTimePasswordData(
            counter,
            emptyBufferStr,
            bufferWithCounterStr,
            hmacStr,
            offset,
            bufferStepsStr,
            hotp,
            res
        )
    }
}
