package com.example.network.model.response

import com.example.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageListResponse(
    val list: List<Message>
)
