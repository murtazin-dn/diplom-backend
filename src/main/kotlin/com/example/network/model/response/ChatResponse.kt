package com.example.network.model.response

import com.example.model.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: Long,
    val firstUserId: Long,
    val secondUser: UserInfoResponse
)
