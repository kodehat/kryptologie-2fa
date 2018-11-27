package de.codehat.tfademo

import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

class TimeBasedOneTimePasswordGenerator(
    timeStep: Long,
    timeStepUnit: TimeUnit,
    passwordLength: Int,
    algorithm: String) : HmacOneTimePasswordGenerator(passwordLength, algorithm) {

    companion object {
        const val TOTP_ALGORITHM_HMAC_SHA1 = "HmacSHA1"
        const val TOTP_ALGORITHM_HMAC_SHA256 = "HmacSHA256"
        const val TOTP_ALGORITHM_HMAC_SHA512 = "HmacSHA512"
    }

    private var timeStepMillis: Long = 0

    constructor(): this(30, TimeUnit.SECONDS)
    constructor(timeStep: Long, timeStepUnit: TimeUnit): this(timeStep, timeStepUnit, HmacOneTimePasswordGenerator.DEFAULT_PASSWORD_LENGTH)
    constructor(timeStep: Long, timeStepUnit: TimeUnit, passwordLength: Int): this(timeStep, timeStepUnit, passwordLength, TOTP_ALGORITHM_HMAC_SHA1)


    init {
        timeStepMillis = timeStepUnit.toMillis(timeStep)
    }

    fun generateOneTimePassword(secretKey: SecretKey, timestamp: Date): OneTimePasswordData {
        return this.generateOneTimePassword(secretKey, timestamp.time / this.timeStepMillis)
    }

    fun getTimeStep(timeUnit: TimeUnit): Long {
        return timeUnit.convert(this.timeStepMillis, TimeUnit.MILLISECONDS)
    }

}