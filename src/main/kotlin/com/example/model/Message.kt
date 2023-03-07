package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long
)
