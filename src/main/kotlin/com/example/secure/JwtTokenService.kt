package com.example.secure

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.User
import java.util.*

object JwtTokenService: TokenService {

    private const val secret = "bkFwb2xpdGE2OTk5"
    private const val issuer = "https://0.0.0.0:8080"
    private const val audience = "https://0.0.0.0:8080/secret"
    private const val refreshValidityInMs: Long = 3600000L * 24L * 30L // 30 days
    private val algorithm = Algorithm.HMAC512(secret)

    override fun generate(userId: Long): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshValidityInMs))
        .sign(algorithm)
}

interface TokenService {

    fun generate(
        userId: Long
    ): String
}