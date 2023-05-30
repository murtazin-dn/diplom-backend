package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long,
    val type: MessageType,
    val isRead: Boolean,
    val images: List<String>
)

@Serializable
enum class MessageType {
    IN, OUT
}