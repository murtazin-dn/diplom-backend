package com.example.model

import com.example.network.model.response.MessageResponse
import kotlinx.serialization.Serializable

@Serializable
data class ChatPreview(
    val chatId: Long,
    val userId: Long,
    val name: String,
    val surname: String,
    val icon: String?,
    val unreadMessagesCount: Long,
    val lastMessage: MessageResponse

)
