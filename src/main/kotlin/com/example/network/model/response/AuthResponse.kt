package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)