package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class FCMTokenRequest(
    val token: String
)
