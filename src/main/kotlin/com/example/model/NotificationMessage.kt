package com.example.model

import com.example.network.model.response.ChatResponse
import com.example.network.model.response.UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
data class NotificationMessage(
    val id: Long,
    val chat: ChatResponse,
    val user: UserInfoResponse,
    val text: String,
    val date: Long
)