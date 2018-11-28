package de.codehat.tfademo

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.spec.SecretKeySpec

fun main(args: Array<String>) {
    val app = Javalin.create().apply {
        val port = System.getProperty("server.port") ?: "8080"
        port(port.toInt())
        enableStaticFiles("/public")
    }.start()

    app.routes {
        get("/hotp/:password_length/:algorithm/:secret_key/:counter") {
            val passwordLength = it.pathParam("password_length").toInt()
            val algorithm = it.pathParam("algorithm")
            val secretKey = it.pathParam("secret_key")
            val counter = it.pathParam("counter").toLong()

            val hotp = HmacOneTimePasswordGenerator(passwordLength, algorithm)
            val data = hotp.generateOneTimePassword(SecretKeySpec(secretKey.toByteArray(StandardCharsets.US_ASCII), "RAW"), counter)
            it.json(data)
        }
        get("/totp/:password_length/:algorithm/:secret_key/:timestamp/:time_step") {
            val passwordLength = it.pathParam("password_length").toInt()
            val algorithm = it.pathParam("algorithm")
            val secretKey = it.pathParam("secret_key")
            val timestamp = Date(it.pathParam("timestamp").toLong())
            val timeStep = it.pathParam("time_step").toLong()

            val totp = TimeBasedOneTimePasswordGenerator(timeStep, TimeUnit.SECONDS, passwordLength, algorithm)
            val data = totp.generateOneTimePassword(SecretKeySpec(secretKey.toByteArray(StandardCharsets.US_ASCII), "RAW"), timestamp)
            it.json(data)
        }
    }
}