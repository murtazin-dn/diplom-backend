package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatPreview(
    val chatId: Long,
    val userId: Long,
    val name: String,
    val surname: String,
    val icon: String?,
    val lastMessageText: String?,
    val lastMessageDate: Long?
)
