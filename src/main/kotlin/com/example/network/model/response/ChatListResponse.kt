package com.example.network.model.response

import com.example.model.ChatPreview
import kotlinx.serialization.Serializable

@Serializable
data class ChatListResponse(
    val list: List<ChatPreview>
)
