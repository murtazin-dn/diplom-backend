package com.example.model

import com.example.network.model.response.MessageResponse
import com.example.network.model.response.MessageType
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long,
    val isRead: Boolean,
    val images: MutableList<String>
) {
    fun messageToMessageResponse(userId: Long) = MessageResponse(
        id = this.id,
        chatId = this.chatId,
        userId = this.userId,
        text = this.text,
        date = this.date,
        isRead = this.isRead,
        type = if (userId == this.userId) MessageType.OUT else MessageType.IN,
        images = this.images
    )
}


