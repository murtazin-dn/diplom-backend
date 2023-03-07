package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long
)
