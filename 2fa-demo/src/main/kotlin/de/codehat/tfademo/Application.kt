package de.codehat.tfademo

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

fun main(args: Array<String>) {
    val app = Javalin.create().apply {
        port(8080)
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
    }
}