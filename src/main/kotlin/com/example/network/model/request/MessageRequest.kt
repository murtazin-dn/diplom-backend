package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val text: String,
    val images: List<String>
)
