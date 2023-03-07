package com.example.network.model.response

import com.example.database.model.users.Chats
import com.example.database.model.users.Chats.autoIncrement
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: Long,
    val firstUserId: Long,
    val secondUserId: Long
)
